#!/usr/bin/env python
# encoding: utf-8

import sys
import os
import json

def options_path():
   return os.environ['MEMOIZEIT_OPTIONS_FILE']

def set_options_file(path):
   os.environ['MEMOIZEIT_OPTIONS_FILE'] = path
   
def options():
   opts_file_path = options_path()
   json_data=open(opts_file_path)
   data = json.load(json_data)
   json_data.close()
   return data

def time_dir():
   return options()['dirs']['time']

def tuples_dir():
   return options()['dirs']['tuples']

def bytecode_dir():
   return options()['dirs']['bytecode']

def time_profile_file():
   return options()['time']['profile']

def time_output_file():
   return options()['time']['output']

def time_total_file():
   return options()['time']['total']

def tuples_output_file():
   return options()['tuples']['output']

def tuples_depths_file():
   return options()['tuples']['depths']   

def tuples_final_file():
   return options()['tuples']['final']
      
def tuples_max_depth_file():
   return options()['tuples']['max_depth']

def white_list_file():
   return options()['files']['white_list']

def black_list_file():
   return options()['files']['black_list']

def statistics_file():
   return options()['files']['statistics']

def options_file():
   return options()['files']['options']

def log_file():
   return options()['files']['log']

def std_out_file():
   return options()['files']['out']

def std_err_file():
   return options()['files']['err']
