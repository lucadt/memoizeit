#!/usr/bin/env python
# encoding: utf-8

import sys
import os
import getopt
import datetime
import shlex
import shutil
import tarfile
import subprocess
#
import experiment
import program
import options
import commons

PROGRAMS = {
   'original':'MP_checks.xml'
}

class CheckStyleProgram(program.Program):
   
   def _prefix(self):
      return self._test

   def _path(self):
      return 'checkstyle'
          
   @staticmethod
   def create(path, test):
      return CheckStyleProgram(path, test, PROGRAMS[test])

   def __init__(self, path, test, checks):
      self._test = test
      self._checks = checks
      super(CheckStyleProgram, self).__init__(path, 'com.puppycrawl')

   @property
   def test(self):
      return 'src_dist' 
    
   @property
   def checks(self):
      return self._checks
  
   @property
   def dir(self):
      return commons.programs_path() + '/' + 'checkstyle-5.6'
   
   @property
   def dist(self):
      return self.dir + '/' + self.test
      
   @property
   def lib(self):
      libs = [
         'ant.jar', 
         'antlr-2.7.7.jar', 
         'commons-beanutils-core-1.8.3.jar', 
         'commons-cli-1.2.jar',
         'commons-logging-1.1.1.jar',
         'google-collections-1.0.jar',
         'tools.jar']
      libs_path = '%s/lib/' % self.dist
      libs_w_path = [libs_path + lib for lib in libs]
      libs_str = ':'.join(libs_w_path)
      target_str = '%s/target/checkstyle-5.6.jar' % self.dist
      return target_str + ':' + libs_str
      
   @property
   def source(self):
      find_command = 'find %s/src_dist/src/checkstyle -name "*.java"' % self.dir
      find_files = subprocess.check_output(shlex.split(find_command))
      return ' '.join(find_files.strip().split('\n'))
         
   def _get_program_options(self):
      return shlex.split('-cp %s com.puppycrawl.tools.checkstyle.Main -c %s/binary_dist/%s -o /dev/null %s' % (self.lib, self.dir, self.checks, self.source))
   