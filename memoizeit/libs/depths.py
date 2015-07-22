#!/usr/bin/env python
# encoding: utf-8
"""
parse_depths.py

Created by Luca Della Toffola on 2014-01-02.
Copyright (c) 2013 ETH Zurich. All rights reserved.
"""
#
import sys
import os
import getopt
import csv
import argparse
#
from java.lang import String
#
def read_methods(file):
   names = {}
   for line in file:
      nline = line.strip()
      jstr = String(nline)
      hcode = jstr.hashCode()
      names[hcode] = nline
   return names
#
def read_trace(file):
   depths = {}
   for line in file:
      aline = line.strip().split(';')
      mid = int(aline[1])
      mdepth = int(aline[-1])
      if mid not in depths:
         depths[mid] = 0
      depths[mid] = max(depths[mid], mdepth)
   return depths
#  
def get_killed_methods(trace, methods, depth):
   
   with open(methods) as f:
      names = read_methods(f)

   with open(trace, 'r', 4096) as f:
      depths = read_trace(f)
   
   killed = []
   for mid, mdepth in depths.items():
      if mdepth < depth:
         mname_split = names[mid].split(':')
         mname = ':'.join(mname_split[1:])
         killed.append(mname)
   return killed 
#
def parse(trace, methods, output):
   #
   with open(methods) as f:
      names = read_methods(f)
   #   
   with open(trace, 'r', 4096) as f:
      depths = read_trace(f)
   #
   with open(output, 'w', 4096) as csvfile:
      dwriter = csv.writer(csvfile)
      for mid, mdepth in depths.items():     
         dwriter.writerow([str( names[mid] ), mdepth])
#
def main(argv=None):
   
   parser = argparse.ArgumentParser(description = 'CacheIt - Parse depth file.')
   parser.add_argument('--trace', dest='trace', help='Specifies the trace of depths for each method')
   parser.add_argument('--methods', dest='methods', help='Specify the method trace')
   parser.add_argument('--output', dest='output', help='Specifies the output csv file')
   
   args = parser.parse_args()
 
   try:
      parse(args.trace, args.methods, args.output)
      return 1
   except:
      return -1
   
if __name__ == "__main__":
   sys.exit(main())
