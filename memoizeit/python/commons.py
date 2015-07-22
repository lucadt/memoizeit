#!/usr/bin/env python
# encoding: utf-8

import sys
import os
import shlex
import json

TRUE_STRING = 'True'
FALSE_STRING = 'False'
NONE_STRING = 'None'

def log(message):
   print '[%s] -- %s' % ('MemoizeIt', message)
   
def set_general_path(path):
   os.environ['MEMOIZEIT_PATH'] = path   
   
def general_path():
   return os.environ['MEMOIZEIT_PATH']

def set_general_path(path):
   os.environ['MEMOIZEIT_PATH'] = path

def programs_path():
   return os.environ['MEMOIZEIT_PROGRAMS_PATH']

def set_programs_path(path):
   os.environ['MEMOIZEIT_PROGRAMS_PATH'] = path

def libs_path():
   return os.environ['MEMOIZEIT_LIBS_PATH']

def set_libs_path(path):
   os.environ['MEMOIZEIT_LIBS_PATH'] = path
 
def jars_path():
   return os.environ['MEMOIZEIT_JARS_PATH']

def set_jars_path(path):
   os.environ['MEMOIZEIT_JARS_PATH'] = path
   
def profiles_path():
   return os.environ['MEMOIZEIT_PROFILES_PATH']

def set_profiles_path(path):
   os.environ['MEMOIZEIT_PROFILES_PATH'] = path

def callgraphs_path():
   return os.environ['MEMOIZEIT_CALLGRAPHS_PATH']

def set_callgraphs_path(path):
   os.environ['MEMOIZEIT_CALLGRAPHS_PATH'] = path

def set_log_depth(flag):
   if flag == True:
      os.environ['MEMOIZEIT_LOG_DEPTH'] = TRUE_STRING
   else:
      os.environ['MEMOIZEIT_LOG_DEPTH'] = FALSE_STRING
      
def log_depth():
   return os.environ['MEMOIZEIT_LOG_DEPTH'] == TRUE_STRING

def set_profile_exaustive(flag):
   if flag == True:
      os.environ['MEMOIZEIT_PROFILE_EXHAUSTIVE'] = TRUE_STRING
   else:
      os.environ['MEMOIZEIT_PROFILE_EXHAUSTIVE'] = FALSE_STRING
      
def profile_exaustive():
   return os.environ['MEMOIZEIT_PROFILE_EXHAUSTIVE'] == TRUE_STRING

def set_filter_using_time(flag):
   if flag == True:
      os.environ['MEMOIZEIT_USE_TIME_PROFILES'] = TRUE_STRING
   else:
      os.environ['MEMOIZEIT_USE_TIME_PROFILES'] = FALSE_STRING
     
def filter_using_time():
    return os.environ['MEMOIZEIT_USE_TIME_PROFILES'] == TRUE_STRING

def set_filter_using_fields(flag):
   if flag == True:
      os.environ['MEMOIZEIT_USE_FIELD_PROFILES'] = TRUE_STRING
   else:
      os.environ['MEMOIZEIT_USE_FIELD_PROFILES'] = FALSE_STRING

def filter_using_fields():
    return os.environ['MEMOIZEIT_USE_FIELD_PROFILES'] == TRUE_STRING

def increment_function():
   return os.environ['MEMOIZEIT_PROFILE_INCREMENT_FUNCTION']  

def set_increment_function(fun):
   os.environ['MEMOIZEIT_PROFILE_INCREMENT_FUNCTION'] = fun  
   
def profiler_jar():
   return jars_path() + '/' + 'profiler.jar'

def library_jar():
   return jars_path() +  '/' + 'lib.jar'

def analysis_jar():
   return jars_path() +  '/' + 'analysis.jar'
   
def dist_jar():
   return jars_path() +  '/' + 'dist.jar'

def jython_jar():
   return libs_path() +  '/' + 'jython-standalone-2.7-b1.jar'

def depths_script_py():
   return libs_path() +  '/' + 'depths.py'
               
def agent_internal_libs():
   return '%s:%s:%s' % (dist_jar(), profiler_jar(), library_jar())

def agent_options(agent_jar):
   return shlex.split('-noverify -Xbootclasspath/p:%s -javaagent:%s' % (agent_internal_libs(), jars_path()+'/'+agent_jar))

def agent_field_options():
   return agent_options('agent_fields.jar')
   
def agent_time_options():
   return agent_options('agent_time.jar')

def agent_tuples_options():
   return agent_options('agent_tuples.jar')

def analyis_options():
   return shlex.split('-Xbootclasspath/p:%s:%s' % (analysis_jar(), agent_internal_libs()))

def scala_options():
   return shlex.split('-Xbootclasspath/p:%s' % scala_libs())

def scala_libs():
   return '%s:%s:%s:%s' % (cpb_jar(), scala_jar(), super_csv_jar(), callgraphs_jar())

def cpb_jar():
   return libs_path() + '/' + 'cpb.jar'

def scala_jar():
   return libs_path() +  '/' +  'scala-library.jar'

def super_csv_jar():
   return libs_path() +  '/' +  'super-csv-2.0.1.jar'

def callgraphs_jar():
   return libs_path() +  '/' +  'callgraphs.jar' 
