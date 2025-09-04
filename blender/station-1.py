import bpy
from bpy_extras import view3d_utils
import colorsys
import random

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

station_x = 128
station_y = 128
station_z = 128
station_distance = 256

def create_station( x=0, y=0, z=0, size=1 ):
    door_x = 16
    door_y = 16
    door_z = 16
    wall_width = 1

    # outer
    station_mat = lib.create_material( name="m.station", color=lib.hex_to_rgba("#FFFFFFFF"), metallic=0.5, roughness=.5)
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y, z-station_z/2), scale=(station_x, station_y, station_z))
    station = bpy.context.active_object
    station.name = 'station'
    station.data.materials.append(station_mat)
    # enable smooth shading
    bpy.ops.object.shade_smooth()
    # enable Auto Smooth so edges stay sharp
    mod = station.modifiers.new(name="Smooth by Angle", type='NODES')
    bpy.ops.object.shade_auto_smooth(use_auto_smooth=True, angle=1.0472)

    # inner    
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y, z-station_z/2), scale=(station_x-wall_width*2, station_y-wall_width*2, station_z-wall_width*2))
    station_inner = bpy.context.active_object
    station_inner.name = 'station_inner'
    station_inner.hide_set(True)

    lib.create_boolean_modifier( root = station, name="m1", operation = 'DIFFERENCE', object = station_inner, apply=True )
    lib.create_bevel_modifier( root = station, name="m3", segments=3, width=2, apply=True )

    # hanger doors
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y, z), scale=(door_x, door_y, door_z))
    neg_door = bpy.context.active_object
    neg_door.name = 'station'
    neg_door.hide_set(True)

    lib.create_boolean_modifier( root = station, name="m4", operation = 'DIFFERENCE', object = neg_door )
      
def create_cube(mat, x, y, z, size=1):
    bpy.ops.mesh.primitive_cube_add(size=size, location=(x, y, z))
    cube = bpy.context.active_object
    cube.data.materials.append(mat)
    bevel = lib.create_bevel_modifier( root = cube, name="m1", segments=3, width=2, apply=True )
    # enable smooth shading
    bpy.ops.object.shade_smooth()
    # enable Auto Smooth so edges stay sharp
    mod = cube.modifiers.new(name="Smooth by Angle", type='NODES')
    bpy.ops.object.shade_auto_smooth(use_auto_smooth=True, angle=1.0472)
    return cube

def create_round_pipe(mat, start, end, radius=0.1):
    """Create a pipe (cylinder) between two cube centers"""
    import mathutils
    start = mathutils.Vector(start)
    end = mathutils.Vector(end)
    direction = end - start
    length = direction.length
    midpoint = (start + end) / 2

    # Create cylinder
    bpy.ops.mesh.primitive_cylinder_add(
        radius=radius, 
        depth=length, 
        location=midpoint,
        vertices=16
    )
    pipe = bpy.context.active_object
    pipe.data.materials.append(mat)
    # enable smooth shading
    bpy.ops.object.shade_smooth()
    # enable Auto Smooth so edges stay sharp
    mod = pipe.modifiers.new(name="Smooth by Angle", type='NODES')
    bpy.ops.object.shade_auto_smooth(use_auto_smooth=True, angle=1.0472)

    # Rotate cylinder to align with direction
    pipe.rotation_mode = 'QUATERNION'
    pipe.rotation_quaternion = direction.to_track_quat('Z', 'Y')

    return pipe

def create_square_pipe(mat, start, end, radius=0.1):
    """Create a pipe (cylinder) between two cube centers"""
    import mathutils
    start = mathutils.Vector(start)
    end = mathutils.Vector(end)
    direction = end - start
    length = direction.length
    midpoint = (start + end) / 2

    # Create cylinder
    bpy.ops.mesh.primitive_cube_add(size=1, location=midpoint, scale=(radius, radius, length) )
    pipe = bpy.context.active_object
    pipe.data.materials.append(mat)
    # enable smooth shading
    bpy.ops.object.shade_smooth()
    # enable Auto Smooth so edges stay sharp
    mod = pipe.modifiers.new(name="Smooth by Angle", type='NODES')
    bpy.ops.object.shade_auto_smooth(use_auto_smooth=True, angle=1.0472)

    # Rotate cylinder to align with direction
    pipe.rotation_mode = 'QUATERNION'
    pipe.rotation_quaternion = direction.to_track_quat('Z', 'Y')
    lib.create_bevel_modifier( root = pipe, name="m3", segments=3, width=2, apply=True )

    return pipe

def create_station_neighbors(station_mat, pipe_mat, station_size, station_distance, radius):
    grid = {}
    positions = []

    # Step 1: Generate random cubes in grid
    for z in range(0, -4, -1):
        for x in range(-2, 3):
            for y in range(-2, 3):
                pos = (x, y, z)
                if pos == (0, 0, 0) or random.randint(0, 1) == 1:
                    grid[pos] = True

    # Step 2: Remove isolated cubes (no neighbors)
    valid_grid = {}
    for (x, y, z) in grid:
        # 6-connected neighbors
        neighbors = [
            (x+1, y, z), (x-1, y, z),
            (x, y+1, z), (x, y-1, z),
            (x, y, z+1), (x, y, z-1)
        ]
        if any(n in grid for n in neighbors):
            valid_grid[(x, y, z)] = True
    grid = valid_grid

    # Ensure (0,0,0) always exists
    grid[(0,0,0)] = True

    # Step 3: Create cubes
    created_cubes = {}
    for (x, y, z) in grid:
        loc = [x*station_distance, y*station_distance, z*station_distance-station_z/2]
        if x != 0 or y != 0 or z != 0:
            cube = create_cube(station_mat[random.randint(0, len(station_mat)-1)], *loc, station_size)
        loc[2] += station_size/4
        created_cubes[(x,y,z)] = tuple(loc)

    # Step 4: Create pipes between neighbors
    for (x, y, z), loc in created_cubes.items():
        neighbors = [
            (x+1, y, z), (x-1, y, z),
            (x, y+1, z), (x, y-1, z),
            (x, y, z+1), (x, y, z-1)
        ]
        for n in neighbors:
            if n in created_cubes and n > (x,y,z):  # avoid duplicates
                create_round_pipe(pipe_mat, loc, created_cubes[n], radius=radius)
                #create_square_pipe(pipe_mat, loc, created_cubes[n], radius=radius)
                
# main script
lib.clear_scene()
station_mat1 = lib.create_material( name="m.station1", color=lib.hex_to_rgba("#00614eff"), metallic=0.5, roughness=.5)
station_mat2 = lib.create_material( name="m.station2", color=lib.hex_to_rgba("#b00233ff"), metallic=0.5, roughness=.5)
station_mat3 = lib.create_material( name="m.station3", color=lib.hex_to_rgba("#006ab6ff"), metallic=0.5, roughness=.5)
station_mat4 = lib.create_material( name="m.station4", color=lib.hex_to_rgba("#404853ff"), metallic=0.5, roughness=.5)

#pipe_mat = lib.create_material( name="m.pipe", color=lib.hex_to_rgba("#404080FF"), metallic=0.5, roughness=.1)
pipe_mat = lib.create_material( name="m.pipe", color=lib.hex_to_rgba("#FFA500FF"), metallic=0.5, roughness=.5)

create_station_neighbors( station_mat=[station_mat1,station_mat2,station_mat3,station_mat4], pipe_mat=pipe_mat, station_size=128, station_distance=128+64, radius=20 )
create_station()


