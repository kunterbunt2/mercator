import bpy
from bpy_extras import view3d_utils
import colorsys

# -------------------------------------------------------
import os
import sys
import importlib
# Path where the blend file is saved
blend_dir = os.path.dirname(bpy.data.filepath)
if blend_dir not in sys.path:
    sys.path.append(blend_dir)
import lib
importlib.reload(lib)
# -------------------------------------------------------

# main script
lib.clear_scene()
