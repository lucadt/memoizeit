#!/usr/bin/env python
# encoding: utf-8

import sys
import os
import tempfile
import tarfile
import shutil
import shlex
import datetime
import subprocess
import time
import copy
import csv
#
import options
import commons
import config
#

class Experiment(object):
   
   @property
   def program(self):
      return self._program
   
   @property
   def folder(self):
      return self.program.options.folder
         
   def __init__(self, program):
      self._program = program
                 
   def _write_options_to_file(self, directory, options):
      ops_path = '%s/%s' % (directory, config.options_file())
      with open(ops_path, 'a+') as my_file:
         my_file.write('\n'.join(options.as_list))
         my_file.write('\n')
   
   def _move_file(self, from_file, to_file):
      shutil.move(from_file, to_file)
   
   def _copy_file(self, from_file, to_file):
      shutil.copyfile(from_file, to_file)
      
   def _copy_time_profile(self):
      os.makedirs('%s/%s' % (self.folder, config.time_dir()))
      to_file = '%s/%s/%s' % (self.folder, config.time_dir(), config.time_profile_file())
      self._copy_file(self.program.profile, to_file)
        
   def _copy_time_output_to_white_list(self):
      from_file = '%s/%s/%s' % (self.folder, config.time_dir(), config.time_output_file())
      to_file = '%s/%s' % (self.folder, config.white_list_file())
      self._copy_file(from_file, to_file)

   def _read_file(self, file):
      with open(file) as my_file:
         lines = [line.strip() for line in my_file]
      filter(lambda line: len(line) > 0, lines)
      return lines
   
   def _write_file(self, file, methods):
      with open(file, 'w+') as f:
         f.write('\n'.join(methods))
   
   def _count_lines(self, file):
      with open(file, 'r') as f:
         return sum(1 for _ in f)
   
   def _copy_black_list(self):
      from_file = '%s/memoizeit/%s' % (commons.general_path(), config.black_list_file())
      to_file = '%s/%s' % (self.folder, config.black_list_file())
      self._copy_file(from_file, to_file)
      
   def _filter_with_black_list(self):
      self._copy_black_list()
      white_file = '%s/%s/%s' % (self.folder, config.time_dir(), config.time_output_file())
      black_file = '%s/%s' % (self.folder, config.black_list_file())
      white_methods = self._read_file(white_file)
      black_methods = self._read_file(black_file)
      white_method_package = [line for line in white_methods if line not in black_methods and line.startswith(self.program.options.package)]
      white_method_filtered = [line for line in white_method_package if not '<init>' in line]
      white_list_file = '%s/%s' % (self.folder, config.white_list_file())
      self._write_file(white_list_file, white_method_filtered)
    
   def execute(self):
      self.initial_candidates()
      self.refine_candidates()
      
   def initial_candidates(self):
      #
      self._write_options_to_file(self.folder, self.program.options)
      self._write_options_to_file(self.folder, self.program.time_options)  
      #
      self._copy_time_profile()
      self.program.time_jvisualvm()
      #
      if commons.filter_using_fields() == True:
         self.program.fields()
   
   def refine_candidates(self):
      pass
   
   def ranking(self):
      pass
            
class IterativeExperiment(Experiment):

   def __init__(self, program):
      super(IterativeExperiment, self).__init__(program)

   def _save_white_list(self, next_depth):
      self._copy_file('%s/%s' % (self.folder, config.white_list_file()), '%s/%d.txt' % (self.folder, next_depth))
   
   def _save_current_depth_directory(self, depth):
      self._move_file('%s/%s' % (self.folder, config.tuples_dir()), '%s/%d' % (self.folder, depth))
      self._move_file('%s/%d.txt' % (self.folder, depth) , '%s/%d/%s' % (self.folder, depth, config.white_list_file()))

   def _create_tuples_file(self):
      if commons.profile_exaustive() == True:
         from_file = '%s/%s/%s' % (self.folder, config.tuples_dir(), config.tuples_output_file())
         to_file = '%s/%s' % (self.folder, config.tuples_final_file())                
         self._copy_file(from_file, to_file)
   
   def _max_depth_reached(self):
      if commons.profile_exaustive() == True:
         return True
      else:
         return not os.path.isfile('%s/%s/%s' % (self.folder, config.tuples_dir(), config.tuples_max_depth_file()))
   
   def _create_filter_depths_trace(self):
      depths_csv = '%s/%s/%s' % (self.folder, config.tuples_dir(), config.tuples_depths_file())
      methods_file = '%s/%s/%s' % (self.folder, config.tuples_dir(), 'methods.txt')
      trace_file = '%s/%s/data/thread_1_main/log_depth.txt' % (self.folder, config.tuples_dir())
      running = 'java -jar %s %s --trace %s --methods %s --output %s' % (commons.jython_jar(), commons.depths_script_py(), trace_file, methods_file, depths_csv)         
      subprocess.call( shlex.split( str(running)) )
   
   def _filter_tuples(self, depth):
      white_list = '%s/%s' % (self.folder, config.white_list_file())      
      tuples_output = '%s/%s/%s' % (self.folder, config.tuples_dir(), config.tuples_output_file())
      depths_csv = '%s/%s/%s' % (self.folder, config.tuples_dir(), config.tuples_depths_file())
      tuples_final = '%s/%s' % (self.folder, config.tuples_final_file())
      hit_rates = '%s/hit_rates_%d.txt' % (self.folder, depth) 
      min_hit_rate = 0.5
      running = 'java -cp %s cpb.RefineCandidateMethods %d %f %s %s %s %s %s' % (commons.scala_libs(), depth, min_hit_rate, tuples_output, depths_csv, tuples_final, hit_rates, white_list)
      self._create_filter_depths_trace()            
      subprocess.call(shlex.split(str(running)))
   
   def _ranking(self, cluster):
      #
      if cluster == True:
         options_cluster = ['0.01', '0.5', 'true']
      else:
         options_cluster = ['0.01', '0.5', 'false']
      #
      time_stats_file = '%s/%s/stats.txt' % (self.folder, config.time_dir())
      time_total_file = '%s/%s/%s' % (self.folder, config.time_dir(), config.time_total_file())
      time_profile_file = '%s/%s/%s' % (self.folder, config.time_dir(), config.time_profile_file())
      tuples_file = '%s/%s' % (self.folder, config.tuples_final_file())       
      #
      call_graph_file = '%s/%s/%s/call_graph.bin' % (commons.callgraphs_path(), self.program.path, self.program.prefix)
      #
      running = 'java -cp %s cpb.RankAndInspect %s %s %s %s %s %s' % (commons.scala_libs(), self.program.prefix, time_stats_file, time_total_file, tuples_file, call_graph_file, time_profile_file)
      subprocess.call(shlex.split(str(running)) + options_cluster)
      #
   def ranking(self):
      #
      commons.log('Ranking -- ' + '"' + self.program.prefix + '"' + '-- *without* clustering"')
      self._ranking(False)
      #
      commons.log('Ranking -- ' + '"' + self.program.prefix + '"' + '-- *with* clustering"')
      self._ranking(True)
      #
   def refine_candidates(self):
      #
      self._filter_with_black_list()
      #
      depth = 1
      #
      use_max_depth = not commons.profile_exaustive()
      #
      get_next_depth = None
      if commons.increment_function() == 'inc1':
         get_next_depth = lambda x: x + 1
      elif commons.increment_function() == 'pow2':
         get_next_depth = lambda x: 2 * x
      else:
         if commons.profile_exaustive() == False:
            raise Exeception('Increment function parameter has wrong value -- ' + commons.increment_function())
         get_next_depth = None
         
      while (True):
         commons.log('Exploring depth ' + str(depth) + ' -- ' + '"' + self.program.prefix + '"')
         self._save_white_list(depth)
         tuple_options = options.TuplesOptions(use_max_depth, depth, True, False)
         self.program.tuples(tuple_options)
         self._write_options_to_file('%s/%s' % (self.folder, config.tuples_dir()), tuple_options)
         stop = False
         if self._max_depth_reached():
            stop = True
         #
         if commons.profile_exaustive() == False:
            self._filter_tuples(depth)
            self._save_current_depth_directory(depth)
         #
         candidates_list_new = '%s/%s' % (self.folder, config.white_list_file())      
         number_of_candidates = self._count_lines(candidates_list_new)
         #
         if number_of_candidates == 0:
            commons.log('No caching candidates left to explore' + ' -- ' + str(depth) + ' -- ' + '"' + self.program.prefix + '"')
            break 
         #
         if stop:
            self._create_tuples_file()
            commons.log('Max depth ' +  str(depth) + 'reached' + ' -- ' + '"' + self.program.prefix + '"')
            break
         depth = get_next_depth(depth)
         
