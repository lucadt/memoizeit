#!/usr/bin/env python
# encoding: utf-8

import sys
import os
#
import config
import commons

class BaseOptions(object):
   
   @property
   def folder(self):
      return self._folder
   
   @property
   def package(self):
      return self._package
   
   @property
   def compressed(self):
      return self._compressed
   
   @property   
   def as_list(self):
      return self._get_default_heap_size() + self._get_base_general_options() + self._get_base_folder_option() + self._get_base_compressed_option() + self._get_base_package_option()
         
   def __init__(self, folder, package, compressed):
      self._folder = folder
      self._package = package
      self._compressed = compressed
   
   def _get_base_general_options(self):
      return ['-Danalysis.project.options=%s' % config.options_path()]
   
   def _get_base_folder_option(self):
      return ['-Danalysis.project.folder=%s' % self._folder]

   def _get_base_compressed_option(self):
      if self.compressed:
         return ['-Danalysis.project.compressed=true']
      else:
         return ['-Danalysis.project.compressed=false']

   def _get_base_package_option(self):
      return ['-Danalysis.project.package=%s' % self._package]

   def _get_default_heap_size(self):
      return ['-Xmx8192m', '-Xms8192m']

class TimeOptions(object):

   def _get_calls_option(self):
      return ['-Danalysis.time.calls=%s' % self.calls]

   def _get_percentage_option(self):
      return ['-Danalysis.time.percentage=%s' % self.percentage]

   def _get_execution_option(self):
      return ['-Danalysis.time.execution=%s' % self.time]

   @property   
   def as_list(self):
      return self._get_execution_option() + self._get_percentage_option() + self._get_calls_option()

   @property   
   def as_list_plain(self):
      return [str(self.time), str(self.percentage), str(self.calls)]

   @property
   def percentage(self):
      return self._percentage / 100.0
       
   @property
   def time(self):
      return self._execution_time
         
   @property
   def calls(self):
      return self._calls
   
   @staticmethod
   def all_methods():
     instance = TimeOptions()
     instance._calls = 0
     instance._percentaget = 0.0
     instance._execution_time = 0
     return instance
 
   @staticmethod
   def default():
     return TimeOptions()

   def __init__(self):
      self._calls = 2
      self._percentage = 1.0
      self._execution_time = 5
      
class TuplesOptions(object):
   
   @property
   def dump_bytecode(self):
      return self._dump_bytecode
   
   @property
   def use_hash_code(self):
      return self._use_hash_code
   
   @property
   def max_depth(self):
      return self._max_depth
      
   @property
   def log_depth(self):
      return self._log_depth
            
   @property
   def use_max_depth(self):
      return self._use_max_depth

   def __init__(self, use_max_depth, max_depth, use_hash_code, dump_bytecode):
      self._use_max_depth = use_max_depth
      self._max_depth = max_depth
      self._use_hash_code = use_hash_code
      self._dump_bytecode = dump_bytecode
      self._log_depth = commons.log_depth()
      
   def _get_dump_bytecode(self):
      if self.dump_bytecode:
         return ['-Danalysis.dump.bytecode=true']
      else:
         return ['-Danalysis.dump.bytecode=false']
         
   def _get_use_max_depth(self):
      if self.use_max_depth:
         return ['-Danalysis.depth.use=true']
      else:
         return ['-Danalysis.depth.use=false']
         
   def _get_use_hash_code(self):
      if self.use_hash_code:
         return ['-Danalysis.dump.hashcode=true']
      else:
         return ['-Danalysis.dump.hashcode=false']

   def _get_log_depth(self):
      if self.log_depth:
         return ['-Danalysis.depth.log=true']
      else:
         return ['-Danalysis.depth.log=false']
            
   def _get_max_depth(self):
      return ['-Danalysis.depth.max=%d' % (self.max_depth)]
      
   @property   
   def as_list(self):
      return self._get_dump_bytecode() + self._get_use_max_depth() + self._get_log_depth() + self._get_max_depth() + self._get_use_hash_code()
