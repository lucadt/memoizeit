#!/usr/bin/env python
# encoding: utf-8

import sys
import os
import getopt
import datetime
import shlex
import shutil
import tarfile
#
import experiment
import program
import options
import commons

BENCHMARKS = { 
   'antlr':'antlr',
   'bloat':'EDU.purdue.cs.bloat', 
   'chart':'org.jfree', 
   'fop':'org.apache.fop', 
   'luindex':'org.apache.lucene', 
   'pmd':'net.sourceforge.pmd'
}

class DacapoProgram(program.Program):
   
   def _prefix(self):
      return '%s_%s' % (self.benchmark, self.size)
      
   def _path(self):
      return 'dacapo'
   
   @staticmethod
   def create(path, benchmark, size):
      return DacapoProgram(path, benchmark, size)

   def __init__(self, path, benchmark, benchmark_size):
      self._benchmark = benchmark
      self._benchmark_size = benchmark_size
      super(DacapoProgram, self).__init__(path, BENCHMARKS[benchmark])
 
   @property
   def lib(self):
      return commons.programs_path() + '/' + 'dacapo-2006-10-MR2.jar'

   @property
   def benchmark(self):
      return self._benchmark 
   
   @property
   def size(self):
      return self._benchmark_size
   
   @property
   def scratch_dir(self):
      return self.options.folder + '/' + 'scratch'
         
   def _program_cleanup(self):
      self._remove_directory(self.scratch_dir)
           
   def _get_program_options(self):
      return shlex.split('-jar %s -s %s %s' % (self.lib, self.size, self.benchmark))
 