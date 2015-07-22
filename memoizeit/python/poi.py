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

EXAMPLES = {
   'loan': 'org.apache.poi.ss.examples.LoanCalculator',
   'perf_hssf': 'org.apache.poi.ss.examples.SSPerformanceTest HSSF 256 256 1',
   'perf_sxssf': 'org.apache.poi.ss.examples.SSPerformanceTest SXSSF 256 256 1',
   'perf_xssf': 'org.apache.poi.ss.examples.SSPerformanceTest XSSF 256 256 1',
   'test_cache': 'junit.textui.TestRunner org.apache.poi.ss.formula.TestEvaluationCache',
   'test_cache_mp': 'junit.textui.TestRunner org.apache.poi.ss.formula.TestEvaluationCacheMP', 
   'read_write_spreadsheet': 'ReadAndWriteSpreadsheet2',
   'big_example': 'org.apache.poi.hssf.usermodel.examples.BigExample',
   'excel_extractor': 'org.apache.poi.hssf.extractor.ExcelExtractor' # Default profiling input
}

class ApachePoiProgram(program.Program):
   
   def _prefix(self):
      return self._example

   def _path(self):
      return 'apache_poi'
      
   @staticmethod
   def create(path, example):
      return ApachePoiProgram(path, example)
   
   def __init__(self, path, example):
      self._example = example
      super(ApachePoiProgram, self).__init__(path, 'org.apache.poi')
   
   @property
   def dir(self):
      return commons.programs_path() + '/' + 'poi-3.9'
      
   @property
   def libs(self):
      
      # this path ./
      poi_libs = [
      'poi-3.9-20121203.jar',
      'poi-examples-3.9-20121203.jar',
      'poi-excelant-3.9-20121203.jar',
      'poi-ooxml-3.9-20121203.jar',
      'poi-ooxml-schemas-3.9-20121203.jar',
      'poi-scratchpad-3.9-20121203.jar'
      ]
      
      poi_libs_w_path = [self.dir + '/' + lib for lib in poi_libs]
      
      # ./lib/ path
      libs = [
      'commons-codec-1.5.jar',
      'commons-logging-1.1.jar',
      'commons-lang-2.4.jar',
      'junit-3.8.1.jar',
      'log4j-1.2.13.jar'
      ]
      
      libs_w_path = [self.dir + '/lib/' + lib for lib in libs]
      
      # ./ooxml-lib path
      oo_xml_libs = [
      'dom4j-1.6.1.jar',
      'stax-api-1.0.1.jar',
      'xmlbeans-2.3.0.jar'
      ]
      
      oo_xml_libs_w_path = [self.dir + '/ooxml-lib/' + lib for lib in oo_xml_libs]
      return ':'.join(poi_libs_w_path + libs_w_path + oo_xml_libs_w_path + [ self.dir + '/mybin'] )
   
   def _copy_file(self, src_file, dest_file):
      if not os.path.isfile(dest_file): shutil.copyfile(src_file, dest_file)
   
   def _program_cleanup(self):
      self._remove_file(self.options.folder + '/' + 'loan-calculator.xlsx')
      self._remove_file(self.options.folder + '/' + 'XSSF_256_256.xlsx')
      self._remove_file(self.options.folder + '/' + 'SXSSF_256_256.xlsx')
      self._remove_file(self.options.folder + '/' + 'HSSF_256_256.xls')
      self._remove_file(self.options.folder + '/' + 'HSSF_256_256.xls')
      self._remove_file(self.options.folder + '/' + 'workbook.xls')
      self._copy_file(self.dir + '/' + 'grades_semantic_checker.xlsx', self.options.folder + '/' + 'grades_semantic_checker.xlsx')
      self._copy_file(self.dir + '/' + 'grades_semantic_checker_extractor.xls', self.options.folder + '/' + 'grades_semantic_checker_extractor.xls')
      
   def _get_program_options(self):
      if self._example == 'read_write_spreadsheet':
         return shlex.split('-cp %s %s %s' % (self.libs, EXAMPLES[self._example], self.options.folder + '/' + 'grades_semantic_checker.xlsx'))
      elif self._example == 'excel_extractor':
         return shlex.split('-cp %s %s -i %s' % (self.libs, EXAMPLES[self._example], self.options.folder + '/' + 'grades_semantic_checker_extractor.xls'))
      else:
         return shlex.split('-cp %s %s' % (self.libs, EXAMPLES[self._example]))
