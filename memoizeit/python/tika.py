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

FILE_TYPES = {
   'pdf': [ 'Tempero2010fh.pdf' ],
   'jars': [ 'checkstyle-5.6-all.jar', 'gson-2.2.2.jar' ],
   'excel': [ 'migr_resfirst.xls', 'migr_resfirst.xlsx' ],
   'word': [ 'pg5200.doc', 'pg5200.docx' ],
   'text': [ 'pg5200.rtf', 'pg5200.txt'],
   'html': [ 'pg5200.html' ],
   'xml': [ 'pg5200.xml' ],
   'odf': [ 'pg5200.odt', 'migr_resfirst.ods' ],
   'epub': [ 'pg5200.epub' ]
}

class ApacheTikaProgram(program.Program):
   
   def _prefix(self):
      return self._file_type

   def _path(self):
      return 'apache_tika'
      
   @staticmethod
   def create(path, file_type):
      return ApacheTikaProgram(path, file_type, 'org.apache.tika')
   
   @staticmethod
   def create_pkg(path, file_type, package):
      return ApacheTikaProgram(path, file_type, package)
   
   def __init__(self, path, file_type, package):
      self._file_type = file_type
      super(ApacheTikaProgram, self).__init__(path, package)
   
   @property
   def dir(self):
      return commons.programs_path() + '/' + 'tika-1.3'

   @property
   def lib(self):
      return self.dir + '/' + 'tika-app/target/tika-app-1.3.jar'

   @property
   def input_files(self):
      return ' '.join([self.dir + '/workloads/' + self._file_type + '/' + file for file in FILE_TYPES[self._file_type]])
      
   def _get_program_options(self):
      return shlex.split('-jar %s -d --text %s' % (self.lib, self.input_files))
