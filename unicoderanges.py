import sys, os, re

for i in range(1, len(sys.argv)):
    with open(sys.argv[i]) as f:
        ranges = []
        for line in f:
            lines = line.strip().split(",", 3)
            if len(lines) < 3: continue
            
            rangeName = lines[-1].replace('"', "").replace(",","").strip()
            min, max = lines[2].split("-", 2)
            
            if rangeName:
                #print("0x%s - 0x%s --> %s" % (min, max, rangeName))
                print("    unicodeRanges.put(new IntegerRange(0x%s, 0x%s), UnicodeRange.%s);" % \
                      (min, max, rangeName))
                lastRangeName = rangeName
            elif lastRangeName:
                print("    unicodeRanges.put(new IntegerRange(0x%s, 0x%s), UnicodeRange.%s);" % \
                      (min, max, lastRangeName))
            