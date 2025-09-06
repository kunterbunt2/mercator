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
distance_from_edge = 8
tower_x = 8
tower_y = 8
tower_z = 16

def create_radar( name, location, material ):
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(location[0],location[1],location[2]+.5), scale=(.05, 5, 1))
    cube = bpy.context.active_object
    cube.name = name
    cube.data.materials.append(material)
    # Set the cube's initial rotation (frame 1)
    cube.rotation_euler = (0, 0, 0)  # x, y, z
    cube.keyframe_insert(data_path="rotation_euler", frame=1, index=2)  # index=2 is Z-axis

    # Set final rotation (frame 100)
    cube.rotation_euler = (0, 0, 6.28319)  # 360° in radians
    cube.keyframe_insert(data_path="rotation_euler", frame=100, index=2)
    # Make it loop seamlessly
    fcurve = cube.animation_data.action.fcurves.find("rotation_euler", index=2)
    fcurve.modifiers.new(type='CYCLES')        

def create_table( name, location, scale, material ):
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=location, scale=scale)
    cube = bpy.context.active_object
    cube.name = name
    cube.data.materials.append(material)
    
def find_faces(root, target_z, tolerance=1e-4):
    """
    Find all faces whose centers lie on the XY plane at a given world Z.
    """
    faces = []
    for face in root.data.polygons:
        # Convert local center to world coordinates
        world_center = root.matrix_world @ face.center
        if abs(world_center.z - target_z) < tolerance:
            faces.append(face.index)
    return faces

def assign_material_to_faces(obj, face_indices, mat):
    # Add material if not already present
    if mat.name not in [m.name for m in obj.data.materials]:
        obj.data.materials.append(mat)

    mat_index = list(obj.data.materials).index(mat)

    # Assign material index directly to faces
    for f in obj.data.polygons:
        if f.index in face_indices:
            f.material_index = mat_index

def assign_material(obj, mat):
    # Add material if not already present
    if mat.name not in [m.name for m in obj.data.materials]:
        obj.data.materials.append(mat)

    mat_index = list(obj.data.materials).index(mat)

    # Assign material index directly to faces
    for f in obj.data.polygons:
        f.material_index = mat_index

def create_tower( x=0, y=0, z=0, scale=(tower_x, tower_y, tower_z) ):
    wall_width = .5
    window_z = 2
    # outer
    tower_mat = lib.create_material( name="m.station", color=lib.hex_to_rgba("#FFFFFFFF"), metallic=0.5, roughness=.5)
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y, z+tower_z/2), scale=(tower_x, tower_y, tower_z))
    tower = bpy.context.active_object
    tower.name = 'tower'
    # IMPORTANT: Apply transforms so vertices reflect true coordinates
#    bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)
    tower.data.materials.append(tower_mat)
#    assign_material( tower, tower_mat )
    # enable smooth shading
    bpy.ops.object.shade_smooth()
    # enable Auto Smooth so edges stay sharp
    mod = tower.modifiers.new(name="Smooth by Angle", type='NODES')
    bpy.ops.object.shade_auto_smooth(use_auto_smooth=True, angle=1.0472)

    # inner    
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y, z-tower_z/2), scale=(tower_x-wall_width*2, tower_y-wall_width*2, tower_z-wall_width*2))
    station_inner = bpy.context.active_object
    station_inner.name = 'station_inner'
    station_inner.hide_set(True)

    lib.create_boolean_modifier( root = tower, name="m1", operation = 'DIFFERENCE', object = station_inner, apply=True )
    lib.create_bevel_modifier( root = tower, name="m3", segments=3, width=2, apply=True )
    
    # window_hole_x
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y, z+tower_z-window_z/2-wall_width), scale=(tower_x*2-wall_width*2, tower_y-wall_width*2, window_z))
    window_hole_x = bpy.context.active_object
    window_hole_x.name = 'window_hole_x'
    window_hole_x.hide_set(True)
    lib.create_boolean_modifier( root = tower, name="m2", operation = 'DIFFERENCE', object = window_hole_x, apply=True )

    # window_hole_y
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y, z+tower_z-window_z/2-wall_width), scale=(tower_x-wall_width*2, tower_y*2-wall_width*2, window_z))
    window_hole_y = bpy.context.active_object
    window_hole_y.name = 'window_hole_y'
    window_hole_y.hide_set(True)
    lib.create_boolean_modifier( root = tower, name="m2", operation = 'DIFFERENCE', object = window_hole_y, apply=True )

#    target_z=z+tower_z-window_z-wall_width
#    faces_at_z = find_faces( root=tower, target_z=target_z, tolerance=.1)
#    floor_mat = lib.create_material( name="floor", color=lib.hex_to_rgba("#FFFF00FF"), metallic=0.5, roughness=0.5 )
#    assign_material_to_faces(tower, faces_at_z, floor_mat)
    
    # window
    window_mat = lib.create_material( name="m.window", color=(0, 0, 0, 1), metallic=1, roughness=0.3, alpha=0.9)
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y, z+tower_z-window_z/2-wall_width), scale=(tower_x-wall_width, tower_y-wall_width, window_z))
    window = bpy.context.active_object
    window.name = 'window'
    window.data.materials.append(window_mat)
    
    # tables
#    table_mat = lib.create_material( name="m.table", color=(0, 0, 1, 1.0), metallic=0.1, roughness=0.1)
#    create_table( 'table', location=(x, y, z+tower_z-window_z-wall_width), scale=(.5,.5,1), material=table_mat )
    # Radar
#    radar_mat = lib.create_material( name="m.radar", color=(1, 1, 1, 1.0), metallic=0.9, roughness=0.5)
#    create_radar( name='radar', location=(x, y, z+tower_z+0.1), material=radar_mat )


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
create_tower( x=-station_x/2+tower_x/2+distance_from_edge, y=station_y/2-tower_y/2-distance_from_edge, z=0, scale=(tower_x, tower_y, tower_z) )

