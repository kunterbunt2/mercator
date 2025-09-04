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

def create_cube( station_mat ):
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y, z-station_z/2), scale=(station_x, station_y, station_z))
    station = bpy.context.active_object
    station.name = 'station'
    station.data.materials.append(station_mat)
    return station

def create_station( x=0, y=0, z=0, f=1 ):
   radius = 256*2
   station_x = radius*2
   station_y = radius*2
   station_z = radius*2
   station_depth = -station_z/2+station_z/4
   door_x = 128
   door_y = 128
   door_z = 64
   wall_width = 16
   segments = 32
   ring_count = 32

   station_mat = lib.create_material( name="m.station", color=lib.hex_to_rgba("#FFFFFFFF"), metallic=0.1, roughness=.5)
   
   # outer
   bpy.ops.mesh.primitive_uv_sphere_add(radius=radius, enter_editmode=False, align='WORLD', location=(x, y, z+station_depth), scale=(1, 1, 1), segments=segments, ring_count=ring_count)
   station_outer = bpy.context.active_object
   station_outer.name = 'station_outer'
   station_outer.data.materials.append(station_mat)

   #north pole
   bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y, z+station_depth+station_z/2), scale=(station_x, station_y, station_z/2))
   north_pole = bpy.context.active_object
   north_pole.name = 'north_pole'
   north_pole.hide_set(True)

   lib.create_boolean_modifier( root = station_outer, name="m4", operation = 'DIFFERENCE', object = north_pole, apply=True )

   # inner    
   bpy.ops.mesh.primitive_uv_sphere_add(radius=radius-wall_width, enter_editmode=False, align='WORLD', location=(x, y, z+station_depth), scale=(1, 1, 1), segments=segments, ring_count=8)
   station_inner = bpy.context.active_object
   station_inner.name = 'station_inner'
   station_inner.data.materials.append(station_mat)
   station_inner.hide_set(True)

   #north pole
   bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y, z+station_depth+station_z/2-1), scale=(station_x, station_y, station_z/2))
   north_pole = bpy.context.active_object
   north_pole.name = 'north_pole'
   north_pole.hide_set(True)

   lib.create_boolean_modifier( root = station_inner, name="m4", operation = 'DIFFERENCE', object = north_pole, apply=True )

   lib.create_boolean_modifier( root = station_outer, name="m1", operation = 'DIFFERENCE', object = station_inner, apply=True )

   #lib.create_bevel_modifier( root = station_outer, name="m3", segments=9, width=10, apply=True )

   


   # hanger doors
   bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y, z+station_depth+station_z/2-station_z/4), scale=(door_x, door_y, door_z))
   neg_door = bpy.context.active_object
   neg_door.name = 'station'
   neg_door.hide_set(True)
   
   lib.create_boolean_modifier( root = station_outer, name="m4", operation = 'DIFFERENCE', object = neg_door, apply=True )


# main script
lib.clear_scene()
create_station()


