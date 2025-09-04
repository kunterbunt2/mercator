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

def create_station( x=0, y=0, z=0, f=1 ):
    station_x = 512
    station_y = 512
    station_z = 64
    door_x = 128
    door_y = 128
    door_z = 64
    wall_width = 16

    # outer
    station_mat = lib.create_material( name="m.station", color=lib.hex_to_rgba("#FFFFFFFF"), metallic=0.5, roughness=.9)
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y, z-station_z/2), scale=(station_x, station_y, station_z))
    station = bpy.context.active_object
    station.name = 'station'
    station.data.materials.append(station_mat)

    # inner    
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y, z-station_z/2), scale=(station_x-wall_width, station_y-wall_width, station_z-wall_width))
    station_inner = bpy.context.active_object
    station_inner.name = 'station_inner'
    station_inner.hide_set(True)

    lib.create_boolean_modifier( root = station, name="m1", operation = 'DIFFERENCE', object = station_inner, apply=True )
    lib.create_bevel_modifier( root = station, name="m3", segments=9, width=10, apply=True )


    # hanger doors
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y, z), scale=(door_x, door_y, door_z))
    neg_door = bpy.context.active_object
    neg_door.name = 'station'
    neg_door.hide_set(True)
    
    lib.create_boolean_modifier( root = station, name="m4", operation = 'DIFFERENCE', object = neg_door )
    


# main script
lib.clear_scene()
create_station()


