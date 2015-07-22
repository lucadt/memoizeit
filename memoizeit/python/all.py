#!/usr/bin/env python
# encoding: utf-8

import sys
import os
import getopt
import datetime
import argparse
#
import dacapo
import soot
import checkstyle
import poi
import tika
import experiment
import commons
import config
#
programs = {}
#
def setup(args):
   #
   experiments_path = '%s/experiments/%s' % (args.path, args.folder)
   #
   commons.log('Experiment path' + '=' + experiments_path)
   #
   config.set_options_file('%s/memoizeit/options.json' % args.path)
   #
   commons.set_general_path(args.path)
   commons.set_programs_path('%s/programs' % args.path)
   commons.set_libs_path('%s/memoizeit/libs' % args.path)
   commons.set_jars_path('%s/memoizeit/jars' % args.path)
   commons.set_profiles_path('%s/profiles' % args.path)
   commons.set_callgraphs_path('%s/callgraphs' % args.path)
   #
   commons.set_log_depth(True)
   #
   commons.set_filter_using_time(args.time)
   commons.set_filter_using_fields(args.fields)
   #
   commons.log('Setting working path to' + '=' + args.path)
   commons.log('Setting to use log depths' + '=' + str(commons.log_depth()))
   commons.log('Setting to filter methods based on time' + '=' + str(commons.filter_using_time()))
   commons.log('Setting to filter methods based on field accesses' + '=' + str(commons.filter_using_fields()))
   #
   programs['Soot'] = soot.SootProgram.create(experiments_path, 'toy')
   programs['Checkstyle'] = checkstyle.CheckStyleProgram.create(experiments_path, 'original')
   #
   programs['DaCapo-antlr'] = dacapo.DacapoProgram.create(experiments_path, 'antlr', 'default')
   programs['DaCapo-bloat'] = dacapo.DacapoProgram.create(experiments_path, 'bloat', 'default') 
   programs['DaCapo-chart'] = dacapo.DacapoProgram.create(experiments_path, 'chart', 'default') 
   programs['DaCapo-fop'] = dacapo.DacapoProgram.create(experiments_path, 'fop', 'default') 
   programs['DaCapo-luindex'] = dacapo.DacapoProgram.create(experiments_path, 'luindex', 'default')
   programs['DaCapo-pmd'] = dacapo.DacapoProgram.create(experiments_path, 'pmd', 'default') 
   #
   programs['Apache-POI'] = poi.ApachePoiProgram.create(experiments_path, 'excel_extractor')
   programs['Apache-Tika-Jar'] = tika.ApacheTikaProgram.create(experiments_path, 'jars')
   programs['Apache-Tika-Excel'] = tika.ApacheTikaProgram.create_pkg(experiments_path, 'excel', 'org.apache.poi')
   #
def run(program, args):   
   #
   commons.log('Profiling' + '=' + '"' + program + '"')
   #
   program = programs[program]
   program.init()
   exp = experiment.IterativeExperiment(program)
   #
   exp.initial_candidates()
   #
   if args.memo == True:
      commons.log('Executing program' + '=' + '"' + program.prefix + '"')
      exp.refine_candidates()
      
def run_ranking(program, args):   
   #
   commons.log('Ranking' + '=' + '"' + program + '"')
   #
   program = programs[program]
   program.init()
   exp = experiment.IterativeExperiment(program)
   exp.ranking()
      
def set_exhaustive_traversal():
   commons.set_profile_exaustive(True)
   commons.set_increment_function(commons.NONE_STRING)
    
def set_iterative_traversal(fun):
   commons.set_profile_exaustive(False)
   commons.set_increment_function(fun)
   
def get_now():
   now = datetime.datetime.now()
   return now.strftime("%Y_%m_%d_%H_%M_%S")
   
def descriptions():
   descs = {}
   descs['Soot'] = 'Program analysis and transformation framework -- Analysis of simple program with large inheritance chain.'
   descs['Checkstyle'] = 'Static code checker -- Analysis of checkstyle source-code.'
   descs['DaCapo-antlr'] = 'DaCapo 2006 benchmark program -- Default workload.'
   descs['DaCapo-bloat'] = 'DaCapo 2006 benchmark program -- Default workload.' 
   descs['DaCapo-chart'] = 'DaCapo 2006 benchmark program -- Default workload.'
   descs['DaCapo-fop'] = 'DaCapo 2006 benchmark program -- Default workload.'
   descs['DaCapo-luindex'] = 'DaCapo 2006 benchmark program -- Default workload.'
   descs['DaCapo-pmd'] = 'DaCapo 2006 benchmark program -- Default workload.'
   descs['Apache-POI'] = 'A library for manipulating MS Office documents -- Converts Excel document to plain text.'
   descs['Apache-Tika-Jar'] = 'A content analysis toolkit -- Outputs Jar document to plain text.'
   descs['Apache-Tika-Excel'] = 'A content analysis toolkit -- Outputs Excel document to plain text.'

   for program, desc in sorted(descs.items()):
      print '"%s": %s' % (program, desc)

def main( argv = None ):
   #
   parser = argparse.ArgumentParser(description = 'MemoizeIt - Finding memoization opportunities.')
   parser.add_argument('--path', dest='path', help='specifies the working directory')
   parser.add_argument('--folder', dest='folder', default=get_now(), help='specify where to save the profiled data')
   parser.add_argument('--time', dest='time', action="store_true", help='Run the initial time profiling phase (Use pre-loaded JVisual VM profiles)')
   parser.add_argument('--fields', dest='fields', action="store_true", help='Run the initial field profiling phase')
   parser.add_argument('--memo', dest='memo', action="store_true", help='Run the tuples profiling phase')
   parser.add_argument('--program', dest='program', help='Run the profiling for provided program')
   parser.add_argument('--ranking', dest='ranking', action="store_true", help='Print the ranking of the candidate methods')
   parser.add_argument('--descriptions', dest='descriptions', action="store_true", help='Print the list of programs that can be analyzed and a short description')
   parser.add_argument('--limit', dest='limit', default='pow2', help='Select iterative mode. Use MemoizeIt algorithm to traverse all the depths argument is depth incremental function [exhaustive, inc1, pow2 (default)]')
   #
   args = parser.parse_args()
   #
   if not args.descriptions == None and args.descriptions == True:
      descriptions()
      return 0
   #
   if args.program == None:
      commons.log('Please specify a program to analyze!')
      return 1
   #
   setup(args)
   #
   if not args.ranking == None and args.ranking == True:
      run_ranking(args.program, args)
      return 0
   #
   if args.limit == 'exhaustive':
      set_exhaustive_traversal()
   else:
      set_iterative_traversal( args.limit )
   #
   run(args.program, args)
   #
if __name__ == "__main__":
   sys.exit(main())
