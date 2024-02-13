local p = 'premake'
local autoconf = p.modules.autoconf
autoconf.cache = {}
autoconf.parameters = ""


---
-- register autoconfigure api.
---
p.api.register {
	name = "autoconfigure",
	scope = "config",
	kind = "table"
}

function check_include(cfg, variable, filename)
	local res = autoconf.cache_compile(cfg, variable, function ()
		p.outln('#include <' .. filename .. '>')
		p.outln('int main(void) { return 0; }')
	end)

	if res.value then
		autoconf.set_value(cfg, variable, 1)
	end
end

function check_type_size(cfg, variable, type, headers, defines)
	check_include(cfg, 'HAVE_SYS_TYPES_H', 'sys/types.h')
	check_include(cfg, 'HAVE_STDINT_H', 'stdint.h')
	check_include(cfg, 'HAVE_STDDEF_H', 'stddef.h')

	local res = autoconf.cache_compile(cfg, variable .. cfg.platform,
		function ()
			if cfg.autoconf['HAVE_SYS_TYPES_H'] then
				p.outln('#include <sys/types.h>')
			end

			if cfg.autoconf['HAVE_STDINT_H'] then
				p.outln('#include <stdint.h>')
			end

			if cfg.autoconf['HAVE_STDDEF_H'] then
				p.outln('#include <stddef.h>')
			end

			autoconf.include_defines(defines)
			autoconf.include_headers(headers)
			p.outln("")
			p.outln("#define SIZE (sizeof(" .. type .. "))")
			p.outln("char info_size[] =  {'I', 'N', 'F', 'O', ':', 's','i','z','e','[',")
			p.outln("  ('0' + ((SIZE / 10000)%10)),")
			p.outln("  ('0' + ((SIZE / 1000)%10)),")
			p.outln("  ('0' + ((SIZE / 100)%10)),")
			p.outln("  ('0' + ((SIZE / 10)%10)),")
			p.outln("  ('0' +  (SIZE     %10)),")
			p.outln("  ']', '\\0'};")
			p.outln("")
			p.outln("int main(int argc, char *argv[]) {")
			p.outln("  int require = 0;")
			p.outln("  require += info_size[argc];")
			p.outln("  (void)argv;")
			p.outln("  return require;")
			p.outln("}")
		end,
		function (e)
			-- if the compile step succeeded, we should have a binary with 'INFO:size[*****]'
			-- somewhere in there.
			local content = io.readfile(e.binary)
			if content then
				local size = string.find(content, 'INFO:size')
				if size then
					e.size = tonumber(string.sub(content, size+10, size+14))
				end
			end
		end
	)

	if res.size then
		autoconf.set_value(cfg, 'HAVE_' .. variable, 1)
		autoconf.set_value(cfg, variable, res.size)
	end
end

function check_struct_has_member(cfg, variable, type, member, headers, defines)
	local res = autoconf.cache_compile(cfg, variable, function ()
		autoconf.include_defines(defines)
		autoconf.include_headers(headers)
		p.outln('int main(void) {')
		p.outln('  (void)sizeof(((' .. type .. '*)0)->' .. member ..');')
		p.outln('  return 0;')
		p.outln('}')
	end)

	if res.value then
		autoconf.set_value(cfg, variable, 1)
	end
end

function check_symbol_exists(cfg, variable, symbol, headers, defines)
	local h = headers
	local res = autoconf.cache_compile(cfg, variable, function ()
		autoconf.include_defines(defines)
		autoconf.include_headers(headers)
		p.outln('int main(int argc, char** argv) {')
		p.outln('  (void)argv;')
		p.outln('#ifndef ' .. symbol)
		p.outln('  return ((int*)(&' .. symbol .. '))[argc];')
		p.outln('#else')
		p.outln('  (void)argc;')
		p.outln('  return 0;')
		p.outln('#endif')
		p.outln('}')
	end)

	if res.value then
		autoconf.set_value(cfg, variable, 1)
	end
end

function autoconf.try_compile(cfg, cpp)
	local ts = autoconf.toolset(cfg)
	if ts then
		return ts.try_compile(cfg, cpp, autoconf.parameters)
	else
		p.warnOnce('autoconf', 'no toolset found, autoconf always failing.')
	end
end


function autoconf.cache_compile(cfg, entry, func, post)
	if not autoconf.cache[entry] then
		local cpp = p.capture(func)
		local res = autoconf.try_compile(cfg, cpp)
		if res then
			local e = { binary = res, value = true }
			if post then
				post(e)
			end
			autoconf.cache[entry] = e
		else
			autoconf.cache[entry] = { }
		end
	end
	return autoconf.cache[entry]
end

function autoconf.toolset(cfg)
	local ts = p.config.toolset(cfg)
	if not ts then
		local tools = {
			-- Actually we always return nil on msc. see msc.lua
			['vs2010']   = p.tools.msc,
			['vs2012']   = p.tools.msc,
			['vs2013']   = p.tools.msc,
			['vs2015']   = p.tools.msc,
			['vs2017']   = p.tools.msc,
			['vs2019']   = p.tools.msc,
			['gmake']    = p.tools.gcc,
			['gmake2']    = p.tools.gcc,
			['codelite'] = p.tools.gcc,
			['xcode4']    = p.tools.clang,
		}
		ts = tools['ACTION']
	end
	return ts
end


function autoconf.set_value(cfg, variable, value)
	cfg.autoconf[variable] = value
end


function autoconf.writefile(cfg, filename)
	if cfg.autoconf then
		local file = io.open(filename, "w+")
		for variable, value in pairs(cfg.autoconf) do
			file:write('#define ' .. variable .. ' ' .. tostring(value) .. ('\n'))
		end
		file:close()
	end
end


function autoconf.include_headers(headers)
	if headers ~= nil then
		if type(headers) == "table" then
			for _, v in ipairs(headers) do
				p.outln('#include <' .. v .. '>')
			end
		else
			p.outln('#include <' .. headers .. '>')
		end
	end
end

function autoconf.include_defines(defines)
	if defines ~= nil then
		if type(defines) == "table" then
			for _, v in ipairs(defines) do
				p.outln('#define ' .. v)
			end
		else
			p.outln('#define ' .. defines)
		end
	end
end

p.override(p.action, 'call', function (base, name)
	local a = p.action.get(name)

	-- store the old callback.
	local onBaseProject = a.onProject or a.onproject

	-- override it with our own.
	a.onProject = function(prj)
		-- go through each configuration, and call the setup configuration methods.
		for cfg in p.project.eachconfig(prj) do
			cfg.autoconf = {}
			if cfg.autoconfigure then
				verbose('Running auto config steps for "%s/%s".', prj.name, cfg.name)
				for file, func in pairs(cfg.autoconfigure) do
					func(cfg)

					if not (file ~= "dontWrite") then
						os.mkdir(cfg.objdir)
						local filename = path.join(cfg.objdir, file)
						autoconf.writefile(cfg, filename)
					end
				end
			end
		end

		-- then call the old onProject.
		if onBaseProject then
			onBaseProject(prj)
		end
	end

	-- now call the original action.call methods
	base(name)
end)
