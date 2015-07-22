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

WORKLOADS = { 
   'toy':'SootCachingTest2', # Profiles the simple toy example
   'soot':'SootCachingTest3' # Profiles the Soot itself
}

class SootProgram(program.Program):
 
   def _prefix(self):
      return self.workload

   def _path(self):
      return 'soot'
          
   @staticmethod
   def create(path, workload):
      return SootProgram(path, 'soot', workload)
   
   def __init__(self, path, package, workload):
      self._workload = workload
      super(SootProgram, self).__init__(path, package)
   
   @property
   def workload(self):
      return self._workload
         
   @property
   def soot_test(self):
      return self.program_path + '/' + WORKLOADS[self.workload] + '.jar'

   @property
   def soot_jar(self):
      return self.program_path + '/' + 'soot-git-ae0cec69c0.jar'
      
   @property
   def program_path(self):
      return commons.programs_path() + '/' + self.path
      
   def _get_program_options(self):
      return shlex.split('-cp %s:%s %s %s %s' % (self.soot_jar, self.soot_test, WORKLOADS[self.workload], self.program_path, self.program_path))
