#!/usr/bin/env python
# encoding: utf-8

import sys
import os
import subprocess
import time
import shlex
import shutil
import csv
#
import commons
import config
import options

class Program(object):
   
   def __init__(self, path, package):
      self._my_path = self._build_path(path)
      self._base_options = options.BaseOptions(self._my_path, package, True)
      
   def _build_path(self, path):
      return '%s/%s/%s' % (path, self.path, self.prefix)
     
   def _profile(self):
      return self._build_path(commons.profiles_path()) + '.csv'
    
   def _program_cleanup(self):
      return None
               
   def _get_program_options(self):
      return None
         
   def _prefix(self):
      return None
      
   def _path(self):
      return None

   def init(self):
      if not os.path.exists(self._my_path): os.makedirs(self._my_path)

   @property
   def profile(self):
      return self._profile()

   @property
   def path(self):
      return self._path()
      
   @property
   def prefix(self):
      return self._prefix()
         
   @property   
   def as_list(self):
      return self._get_program_options()
   
   @property
   def options(self):
      return self._base_options
   
   @property
   def time_options(self):
       if commons.filter_using_time() == True:
           commons.log('Using default time profile options.')
           return options.TimeOptions.default()
       else:
           commons.log('Using ALL_METHODS time profile options.')
           return options.TimeOptions.all_methods()
                  
   def _get_fields_analysis_class(self):
      return ['memoizeit.analysis.fields.Main']
      
   def _get_time_converter_class(self):
      return ['cpb.TimeDataJVisualVMConverter']
   
   def _get_time_filter_class(self):
      return ['cpb.FilterMethodsByTime']
   
   def _get_tuples_analysis_class(self):
      return ['memoizeit.analysis.version.Main']
      
   def _remove_file(self, my_file):
      if os.path.isfile(my_file): 
         os.remove(my_file)
         
   def _remove_directory(self, my_directory):
      if os.path.isdir(my_directory): 
         shutil.rmtree(my_directory)

   def _execute(self, tag, opts):
      self._program_cleanup()   
      f_stdout = open('%s/%s' % (self.options.folder, config.std_out_file()), 'a+')
      f_stderr = open('%s/%s' % (self.options.folder, config.std_err_file()), 'a+')
      args = ['java'] + self.options.as_list + opts
      start_time = time.time()
      subprocess.call(args, stdout=f_stdout, stderr=f_stderr, cwd=self.options.folder)
      end_time = time.time()
      f_stdout.close()
      f_stderr.close()
      with open('%s/%s' % (self.options.folder, config.log_file()) , 'a+') as log_file:
         log = csv.writer(log_file)
         log.writerow(tag + [start_time, end_time])
      
   def fields(self):
      commons.log('Field analysis' + ' -- ' + '"' + self.prefix + '"')
      tag=['fields', 'profile', None, None]
      self._execute(tag, commons.agent_field_options() + self._get_program_options())
      tag=['fields', 'analysis', None, None]
      self._execute(tag, commons.analyis_options() + self._get_fields_analysis_class())
      commons.log('Field analysis' + ' -- ' + '"' + self.prefix + '"' + ' -- ' + 'DONE.')

   def time_jvisualvm(self):
      commons.log('Time analysis (JVisualVM)' + ' -- ' + '"' + self.prefix + '"')
      tag=['time', 'convert', None, None]
      time_profile_file = '%s/%s/%s' % (self.options.folder, config.time_dir(), config.time_profile_file()) 
      time_statistics_file = '%s/%s/%s' % (self.options.folder, config.time_dir(), config.statistics_file()) 
      time_output_file = '%s/%s/%s' % (self.options.folder, config.time_dir(), config.time_output_file())
      time_total_file = '%s/%s/%s' % (self.options.folder, config.time_dir(), config.time_total_file())
      converter_params = [time_profile_file, time_statistics_file, time_total_file]
      self._execute(tag, commons.scala_options() + self._get_time_converter_class() + converter_params)
      tag=['time', 'filter', None, None]
      filter_params = [time_statistics_file, time_total_file, time_output_file]
      self._execute(tag, commons.scala_options() + self._get_time_filter_class() + filter_params + self.time_options.as_list_plain)
      commons.log('Time analysis (JVisualVM)' + ' -- ' + '"' + self.prefix + '"' + ' -- ' + 'DONE.')
         
   def tuples(self, options):
      commons.log('Tuples analysis' + ' -- ' + '"' + self.prefix + '"')
      tag=['tuples', 'profile', options.use_max_depth, options.max_depth]
      self._execute(tag, options.as_list + commons.agent_tuples_options() + self._get_program_options())
      tag=['tuples', 'analysis', options.use_max_depth, options.max_depth]
      self._execute(tag, options.as_list + commons.analyis_options() + self._get_tuples_analysis_class())
      commons.log('Tuples analysis' + ' -- ' + '"' + self.prefix + '"' + ' -- ' + 'DONE.')
