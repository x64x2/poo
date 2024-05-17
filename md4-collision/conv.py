#!/usr/bin/env python3
"""Produce the constraints based on text-input conditions."""

generate_extra_conditions = False
# Copied from "New Message Difference for MD4" by Yu Sasaki and Lei Wang and Kazuo Ohta and Noboru Kunihiro.
# Table 8. Sufficient Conditions and Extra Conditions for New Local Collision.
round_one_conditions = '''b1 1 ¯0 - - - - - - ¯a - - - - - - - - - - - - - a - a - - - ¯a - 0 1
b2 1 ¯0 - ¯0 - ¯a - - - - - - 0 - - - - - - - a - 1 - 0 - - - - - 0 1
b3 1 ¯1 - ¯1 ¯a - ¯a 0 ¯0 - a a 1 - ¯a ¯a ¯a ¯a ¯a ¯a 1 ¯a 0 - 0 - - - ¯1 - 1 0
b4 1 - ¯a - ¯1 ¯0 ¯1 1 a a 1 0 0 - ¯0 ¯1 ¯1 ¯1 ¯1 ¯1 0 ¯1 1 a 1 a a a a - - -
b5 a - - - ¯0 ¯0 ¯0 0 1 1 0 0 0 a ¯1 ¯0 ¯0 ¯0 ¯0 ¯0 0 ¯0 0 1 1 1 1 1 1 - - -
b6 0 - ¯0 - ¯1 - ¯1 1 1 1 1 1 0 0 ¯1 ¯1 ¯1 ¯1 ¯1 ¯1 - ¯1 0 0 0 0 0 0 0 a a -
b7 0 - ¯1 - - - 1 1 0 0 - 0 1 0 - - - - - - - - 0 1 1 1 1 1 1 1 1 -
b8 1 - - - - - a 0 0 0 - 1 0 1 - - - - - 0 - - 0 0 0 0 0 0 1 0 0 -
b9 0 a a - a a 0 1 0 1 - - - - - - - - - a - - 1 1 1 1 0 1 1 1 1 -
b10 0 1 1 - 1 0 0 - 1 1 - - - ¯a - - - - - 1 - ¯0 ¯0 - - - - - - - - -
b11 0 0 0 - 1 1 0 - 1 1 - - - - - - - - - 0 - ¯1 ¯1 - - - - - - - - -
b12 0 1 1 a 0 0 1 - - - - - - ¯1 - - - - - 1 - ¯1 ¯0 - - - - - - - - -
b13 - - 1 0 - - 0 - - - - - - ¯1 - - - - - 0 - ¯0 ¯0 - - - - - - - - -
b14 - - 0 0 - - 0 - - - - - ¯a - - - - - - 0 - ¯1 ¯1 - - - - - - - - -
b15 a - 1 1 ¯b - 1 - - - - - - - - ¯b - - - - - - - - - - - - - - - -
b16 1 - - a ¯0 - - - - - - - ¯c - - - - - - - - - - - - - - - - - - -'''

def main():
    output = []
    for line in round_one_conditions.split('\n'):
        zero, one, eq, neq, neq2 = [], [], [], [], []
        splitted = line.split()
        # Skip bX
        splitted.pop(0)

        idx = 32
        for cond in splitted:
            # The conditions are ordered from MSB to LSB.
            idx -= 1
            if "¯" in cond and not generate_extra_conditions:
                    continue
            if "1" in cond:
                    one.append(str(idx))
            if "0" in cond:
                    zero.append(str(idx))
            if "a" in cond:
                    eq.append(str(idx))
            if "b" in cond:
                    neq.append(str(idx))
            if "c" in cond:
                    neq2.append(str(idx))


        constraints = []
        if len(zero) > 0:
            constraints.append(f"zero({', '.join(zero)})")
        if len(one) > 0:
            constraints.append(f"one({', '.join(one)})")
        if len(eq) > 0:
            constraints.append(f"eq({', '.join(eq)})")
        if len(neq) > 0:
            constraints.append(f"neq({', '.join(neq)})")
        if len(neq2) > 0:
            constraints.append(f"neq2({', '.join(neq2)})")
        output.append(f"{{{', '.join(constraints)}}}")

    print(",\n".join(output) + ",")

main()
