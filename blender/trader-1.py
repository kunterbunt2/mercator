import bpy
from bpy_extras import view3d_utils
import colorsys
import math

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
import minion
importlib.reload(minion)
# -------------------------------------------------------

min_distance = .01

def create_container( x=0, y=0, z=0, f=1 ):
    for cx in range(0, 3):
        bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y-4*f+cx*f, z-1*f), scale=(1*f, 1*f, 1*f) )
        obj = bpy.context.active_object
        obj.name = 'container-'+str(cx)
        mat = create_material(m_name="m.container.plane",m_color=(0.5, 0.5, 1.0, 1.0),m_metallic=0.7,m_roughness=0.2)
        # No Specular input found, skipping it.
        obj.data.materials.append(mat)

def create_radar( name, location, material ):
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=location, scale=(.05, 1, 1))
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
    
def create_cone( name, location, material, f=1, rotation=(0,0,0) ):
    bpy.ops.mesh.primitive_cone_add(radius1=0.21*f, radius2=0.11*f, depth=.5*f, enter_editmode=False, align='WORLD', location=location, scale=(1, 1, 1), vertices=8)
    cone = bpy.context.active_object
    cone.name = name
    bpy.ops.mesh.primitive_cone_add(radius1=0.2*f, radius2=0.1*f, depth=.5*f, enter_editmode=False, align='WORLD', location=location, scale=(1, 1, 1), vertices=8)
    cone_inner = bpy.context.active_object
    cone_inner.hide_set(True)
    m1 = lib.create_boolean_modifier( root=cone, name='m'+name, operation='DIFFERENCE', object=cone_inner, apply=True )
    cone.rotation_euler = rotation
    cone.data.materials.append(material)


def create_thruster( name, x=0, y=0, z=0, f=1, s_orientation='right' ):
    if s_orientation == 'right':
        dx = 1
    else:
        dx = -1

    thruster_mat = lib.create_material( name="m.thruster", color=(1, 1, 1, 1.0), metallic=0.9, roughness=0.5)
    bpy.ops.mesh.primitive_uv_sphere_add(radius=0.5*f, enter_editmode=False, align='WORLD', location=(x, y, z), scale=(1, 1, 1), segments=8, ring_count=8)
    ball = bpy.context.active_object
    ball.name = 'xxx'
    ball.data.materials.append(thruster_mat)

    create_cone( name='cone_top'+name, location=(x, y, z+0.5*f), rotation=(math.radians(180),0,0), material=thruster_mat, f=f )
    create_cone( name='cone_bottom'+name, location=(x, y, z-0.5*f), material=thruster_mat, f=f )
    create_cone( name='cone_back'+name, location=(x, y-0.5*f, z), rotation=(math.radians(270),0,0), material=thruster_mat, f=f )
    create_cone( name='cone_front'+name, location=(x, y+0.5*f, z), rotation=(math.radians(90),0,0), material=thruster_mat, f=f )
    create_cone( name='cone_x'+name, location=(x+dx*0.5*f, y, z), rotation=(0,-dx*math.radians(90),0), material=thruster_mat, f=f )


def create_weld_modifier( root, name, apply=False ):
    m = root.modifiers.new(name=name, type='WELD')
    root.select_set(True)
    if apply:
        bpy.context.view_layer.objects.active = root
        bpy.ops.object.modifier_apply(modifier=m.name)
    return m


def create_ship( x=0, y=0, z=0, f=1 ):

    minion.create_minion(x, y+5*f+0.1, z+1, 0.2)
#    # pilot
#    pilot_head_mat = lib.create_material( name="m.pilot.head", color=(1,1,1,1), metallic=0.5, roughness=0.5)
#    bpy.ops.mesh.primitive_uv_sphere_add(radius=0.2*f, enter_editmode=False, align='WORLD', location=(x, y+6*f+0.1, z+0.3), scale=(1, 1, 1))
#    pilot_head = bpy.context.active_object
#    pilot_head.data.materials.append(pilot_head_mat)
#    # enable smooth shading
#    bpy.ops.object.shade_smooth()
#    # enable Auto Smooth so edges stay sharp
#    mod = pilot_head.modifiers.new(name="Smooth by Angle", type='NODES')
#    bpy.ops.object.shade_auto_smooth(use_auto_smooth=True, angle=1.0472)

#    # pilot body
#    bpy.ops.mesh.primitive_uv_sphere_add(radius=0.2*f, enter_editmode=False, align='WORLD', location=(x, y+6*f+0.1, z-0.1), scale=(2, 1, 1))
#    pilot_body = bpy.context.active_object
#    pilot_body.data.materials.append(pilot_head_mat)
#    # enable smooth shading
#    bpy.ops.object.shade_smooth()
#    # enable Auto Smooth so edges stay sharp
#    mod = pilot_body.modifiers.new(name="Smooth by Angle", type='NODES')
#    bpy.ops.object.shade_auto_smooth(use_auto_smooth=True, angle=1.0472)
#    

    # cockpit
#    cockpit_mat = lib.create_material( name="m.cockpit", color=(0, 0, 0, 1), metallic=1, roughness=0.1, alpha=0.8)
#    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y+6*f+min_distance, z+min_distance), scale=(1*f, 1*f, 1*f))
#    body_cockpit = bpy.context.active_object
#    body_cockpit.name = 'body_cockpit'
#    body_cockpit.data.materials.append(cockpit_mat)
#    lib.create_bevel_modifier( root = body_cockpit, name="b8", segments=3, width=5, apply=True )
#    # enable smooth shading
#    bpy.ops.object.shade_smooth()
#    # enable Auto Smooth so edges stay sharp
#    mod = body_cockpit.modifiers.new(name="Smooth by Angle", type='NODES')
#    bpy.ops.object.shade_auto_smooth(use_auto_smooth=True, angle=1.0472)

    body_mat = lib.create_material( name="m.body", color=lib.hex_to_rgba("#FFA500FF"), metallic=0.1, roughness=.5)
    # cockpit
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y+5*f, z+1), scale=(1*f, 1*f, 1*f))
    cockpit = bpy.context.active_object
    cockpit.name = 'cockpit'
    cockpit.data.materials.append(body_mat)
    # cockpit_hole_x
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y+5*f, z+1+0.1), scale=(2*f, .9*f, .65*f))
    cockpit_hole_x = bpy.context.active_object
    cockpit_hole_x.name = 'cockpit_hole_x'
    cockpit_hole_x.data.materials.append(body_mat)
    cockpit_hole_x.hide_set(True)
    lib.create_boolean_modifier( root = cockpit, name="m2", operation = 'DIFFERENCE', object = cockpit_hole_x, apply=True )
    # cockpit_hole_y
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y+5*f, z+1+0.1), scale=(.9*f, 2*f, .65*f))
    cockpit_hole_y = bpy.context.active_object
    cockpit_hole_y.name = 'cockpit_hole_y'
    cockpit_hole_y.data.materials.append(body_mat)
    cockpit_hole_y.hide_set(True)
    lib.create_boolean_modifier( root = cockpit, name="m2", operation = 'DIFFERENCE', object = cockpit_hole_y, apply=True )
    # cockpit_hole_z
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y+5*f, z+1+0.1), scale=(.9*f, .9*f, 2*f))
    cockpit_hole_z = bpy.context.active_object
    cockpit_hole_z.name = 'cockpit_hole_z'
    cockpit_hole_z.data.materials.append(body_mat)
    cockpit_hole_z.hide_set(True)
    lib.create_boolean_modifier( root = cockpit, name="m2", operation = 'DIFFERENCE', object = cockpit_hole_z, apply=True )


    # body-top
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y, z), scale=(1*f, 11*f, 1*f))
    body_top = bpy.context.active_object
    body_top.name = 'body_top'
    body_top.data.materials.append(body_mat)

    # body-front-right-sholder
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x+1*f, y+5*f, z*f), scale=(1*f, 1*f, 1*f))
    body_front_right = bpy.context.active_object
    body_front_right.name = 'body_front_right'
    body_front_right.data.materials.append(body_mat)
    # enable smooth shading
    bpy.ops.object.shade_smooth()
    # enable Auto Smooth so edges stay sharp
    mod = body_front_right.modifiers.new(name="Smooth by Angle", type='NODES')
    bpy.ops.object.shade_auto_smooth(use_auto_smooth=True, angle=1.0472)
    thruster_front_right = create_thruster( 'thruster_front_right', x+2*f-0.25, y+5*f, z*f, f=0.5 )

    # body-front-left-sholder
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x-1*f, y+5*f, z*f), scale=(1*f, 1*f, 1*f))
    body_front_left = bpy.context.active_object
    body_front_left.name = 'body_front_left'
    body_front_left.data.materials.append(body_mat)
    thruster_front_left = create_thruster( 'thruster_front_left', x-2*f+0.25, y+5*f, z*f, s_orientation='left', f=0.5 )

    # body-back-right-sholder
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x+1*f, y-5*f, z*f), scale=(1*f, 1*f, 1*f))
    body_back_right = bpy.context.active_object
    body_back_right.name = 'body_back_right'
    body_back_right.data.materials.append(body_mat)
    thruster_back_right= create_thruster( 'thruster_back_right', x+2*f-0.25, y-5*f, z*f, f=0.5 )

    # body-back-left-sholder
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x-1*f, y-5*f, z*f), scale=(1*f, 1*f, 1*f))
    body_back_left = bpy.context.active_object
    body_back_left.name = 'body_back_left'
    body_back_left.data.materials.append(body_mat)
    thruster_back_left = create_thruster( 'thruster_back_left', x-2*f+0.25, y-5*f, z*f, s_orientation='left', f=0.5 )
    
    lib.join( objects_to_join=[ "body_top", "body_front_right", "body_front_left", "body_back_right", "body_back_left", 'thruster_front_right','thruster_front_left', "thruster_back_right", "thruster_back_left", "cockpit"])
    #create_weld_modifier( root = body_top, name='w1', apply=True )
    lib.create_remesh( bpy.context.view_layer.objects.active, name='remesh1', octree_depth=8, apply=True )

    # enable smooth shading
    bpy.ops.object.shade_smooth()
    # enable Auto Smooth so edges stay sharp
    mod = body_top.modifiers.new(name="Smooth by Angle", type='NODES')
    bpy.ops.object.shade_auto_smooth(use_auto_smooth=True, angle=1.0472)

    lib.create_bevel_modifier( root = body_top, name="b8", segments=3, width=5, apply=True )

    radar_mat = lib.create_material( name="m.thruster", color=(1, 1, 1, 1.0), metallic=0.9, roughness=0.5)
    create_radar( name='radar', location=(x, y, z+1), material=radar_mat )


# main script
lib.clear_scene()
create_ship()
#create_container()

