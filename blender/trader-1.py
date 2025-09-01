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
# -------------------------------------------------------

def create_container( s_x=0, s_y=0, s_z=0, s_f=8 ):
    for cx in range(0, 3):
        bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y-4*s_f+cx*s_f, z-1*s_f), scale=(1*s_f, 1*s_f, 1*s_f) )
        obj = bpy.context.active_object
        obj.name = 'container-'+str(cx)
        mat = create_material(m_name="m.container.plane",m_color=(0.5, 0.5, 1.0, 1.0),m_metallic=0.7,m_roughness=0.2)
        # No Specular input found, skipping it.
        obj.data.materials.append(mat)

def create_cone( name, location, material, s_f=8, rotation=(0,0,0) ):
    bpy.ops.mesh.primitive_cone_add(radius1=0.21*s_f, radius2=0.11*s_f, depth=.5*s_f, enter_editmode=False, align='WORLD', location=location, scale=(1, 1, 1), vertices=8)
    cone = bpy.context.active_object
    
    cone.name = name
    bpy.ops.mesh.primitive_cone_add(radius1=0.2*s_f, radius2=0.1*s_f, depth=.5*s_f, enter_editmode=False, align='WORLD', location=location, scale=(1, 1, 1), vertices=8)
    cone_inner = bpy.context.active_object
    cone_inner.hide_set(True)
    m1 = lib.create_boolean_modifier( root=cone, name='m'+name, operation='DIFFERENCE', object=cone_inner, apply=True )
    cone.rotation_euler = rotation
    cone.data.materials.append(material)


def create_thruster( name, s_x=0, s_y=0, s_z=0, s_f=8, s_orientation='right' ):
    if s_orientation == 'right':
        dx = 1
    else:
        dx = -1

    thruster_mat = lib.create_material( name="m.thruster", color=(1, 1, 1, 1.0), metallic=0.9, roughness=0.5)
    bpy.ops.mesh.primitive_uv_sphere_add(radius=0.5*s_f, enter_editmode=False, align='WORLD', location=(s_x, s_y, s_z), scale=(1, 1, 1), segments=8, ring_count=8)
    ball = bpy.context.active_object
    ball.name = 'xxx'
    ball.data.materials.append(thruster_mat)

    create_cone( name='cone_top'+name, location=(s_x, s_y, s_z+0.5*s_f), rotation=(math.radians(180),0,0), material=thruster_mat )
    create_cone( name='cone_bottom'+name, location=(s_x, s_y, s_z-0.5*s_f), material=thruster_mat )
    create_cone( name='cone_back'+name, location=(s_x, s_y-0.5*s_f, s_z), rotation=(math.radians(270),0,0), material=thruster_mat )
    create_cone( name='cone_front'+name, location=(s_x, s_y+0.5*s_f, s_z), rotation=(math.radians(90),0,0), material=thruster_mat )
    create_cone( name='cone_x'+name, location=(s_x+dx*0.5*s_f, s_y, s_z), rotation=(0,-dx*math.radians(90),0), material=thruster_mat )
    

    
#    bpy.ops.mesh.primitive_uv_sphere_add(radius=0.3*s_f, enter_editmode=False, align='WORLD', location=(s_x, s_y, s_z+0.5*s_f), scale=(1, 1, 1))
#    ball_top = bpy.context.active_object
#    ball_top.hide_set(True)

#    bpy.ops.mesh.primitive_uv_sphere_add(radius=0.3*s_f, enter_editmode=False, align='WORLD', location=(s_x, s_y+0.5*s_f, s_z), scale=(1, 1, 1))
#    ball_front = bpy.context.active_object
#    ball_front.hide_set(True)

#    bpy.ops.mesh.primitive_uv_sphere_add(radius=0.3*s_f, enter_editmode=False, align='WORLD', location=(s_x, s_y-0.5*s_f, s_z), scale=(1, 1, 1))
#    ball_back = bpy.context.active_object
#    ball_back.hide_set(True)

#    bpy.ops.mesh.primitive_uv_sphere_add(radius=0.3*s_f, enter_editmode=False, align='WORLD', location=(s_x, s_y, s_z-0.5*s_f), scale=(1, 1, 1))
#    ball_bottom = bpy.context.active_object
#    ball_bottom.hide_set(True)

#    bpy.ops.mesh.primitive_uv_sphere_add(radius=0.3*s_f, enter_editmode=False, align='WORLD', location=(s_x+dx*0.5*s_f, s_y, s_z), scale=(1, 1, 1))
#    ball_x = bpy.context.active_object
#    ball_x.hide_set(True)


#    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(s_x, s_y, s_z), scale=(1*s_f, 1*s_f, 1*s_f))
#    thruster = bpy.context.active_object
#    thruster.name = 'thruster'
#    thruster.data.materials.append(thruster_mat)

#    m6 = lib.create_bevel_modifier( root = thruster, name="m6", segments=5, width=0.8, apply=True )
#    m1 = lib.create_boolean_modifier( root=thruster, name="m1", operation='DIFFERENCE', object=ball_top, apply=True )
#    m2 = lib.create_boolean_modifier( root=thruster, name="m2", operation='DIFFERENCE', object=ball_front, apply=True )
#    m3 = lib.create_boolean_modifier( root=thruster, name="m3", operation='DIFFERENCE', object=ball_back, apply=True )
#    m4 = lib.create_boolean_modifier( root=thruster, name="m4", operation='DIFFERENCE', object=ball_bottom, apply=True )
#    m5 = lib.create_boolean_modifier( root=thruster, name="m5", operation='DIFFERENCE', object=ball_x, apply=True )


def create_weld_modifier( root, name, apply=False ):
    m = root.modifiers.new(name=name, type='WELD')
    root.select_set(True)
    if apply:
        bpy.context.view_layer.objects.active = root
        bpy.ops.object.modifier_apply(modifier=m.name)
    return m


def create_ship( s_x=0, s_y=0, s_z=0, s_f=8 ):

    body_mat = lib.create_material( name="m.body", color=lib.hex_to_rgba("#FFA500FF"), metallic=0.1, roughness=.5)
    # body-top
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(s_x, s_y, s_z), scale=(1*s_f, 11*s_f, 1*s_f))
    body_top = bpy.context.active_object
    body_top.name = 'body_top'
    body_top.data.materials.append(body_mat)

    # body-cockpit
    pilot_head_mat = lib.create_material( name="m.pilot.head", color=(1,1,1,1), metallic=0.5, roughness=0.5)
    bpy.ops.mesh.primitive_uv_sphere_add(radius=0.2*s_f, enter_editmode=False, align='WORLD', location=(s_x, s_y+6*s_f+0.1, s_z+0.1), scale=(1, 1, 1))
    pilot_head = bpy.context.active_object
    pilot_head.data.materials.append(pilot_head_mat)


    cockpit_mat = lib.create_material( name="m.cockpit", color=(0, 0, 0, 1), metallic=1, roughness=0.1, alpha=0.8)
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(s_x, s_y+6*s_f+0.1, s_z+0.1), scale=(1*s_f, 1*s_f, 1*s_f))
    body_cockpit = bpy.context.active_object
    body_cockpit.name = 'body_cockpit'
    body_cockpit.data.materials.append(cockpit_mat)
    lib.create_bevel_modifier( root = body_cockpit, name="b8", segments=1, width=.4, apply=True )

    # body-front
#    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(s_x, s_y+5*s_f, s_z-1.5*s_f), scale=(1*s_f, 1*s_f, 2*s_f))
#    body_front = bpy.context.active_object
#    body_front.name = 'body_front'
#    body_front.data.materials.append(body_mat)

    # body-back
#    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(s_x, s_y-5*s_f, s_z-1.5*s_f), scale=(1*s_f, 1*s_f, 2*s_f))
#    body_back = bpy.context.active_object
#    body_back.name = 'body_back'
#    body_back.data.materials.append(body_mat)

    # body-front-right-sholder
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(s_x+1*s_f, s_y+5*s_f, s_z*s_f), scale=(1*s_f, 1*s_f, 1*s_f))
    body_front_right = bpy.context.active_object
    body_front_right.name = 'body_front_right'
    body_front_right.data.materials.append(body_mat)
    thruster_front_right = create_thruster( 'thruster_front_right', s_x+2*s_f, s_y+5*s_f, s_z*s_f )

    # body-front-left-sholder
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(s_x-1*s_f, s_y+5*s_f, s_z*s_f), scale=(1*s_f, 1*s_f, 1*s_f))
    body_front_left = bpy.context.active_object
    body_front_left.name = 'body_front_left'
    body_front_left.data.materials.append(body_mat)
    thruster_front_left = create_thruster( 'thruster_front_left', s_x-2*s_f, s_y+5*s_f, s_z*s_f, s_orientation='left' )

    # body-back-right-sholder
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(s_x+1*s_f, s_y-5*s_f, s_z*s_f), scale=(1*s_f, 1*s_f, 1*s_f))
    body_back_right = bpy.context.active_object
    body_back_right.name = 'body_back_right'
    body_back_right.data.materials.append(body_mat)
    thruster_back_right= create_thruster( 'thruster_back_right', s_x+2*s_f, s_y-5*s_f, s_z*s_f )

    # body-back-left-sholder
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(s_x-1*s_f, s_y-5*s_f, s_z*s_f), scale=(1*s_f, 1*s_f, 1*s_f))
    body_back_left = bpy.context.active_object
    body_back_left.name = 'body_back_left'
    body_back_left.data.materials.append(body_mat)
    thruster_back_left = create_thruster( 'thruster_back_left', s_x-2*s_f, s_y-5*s_f, s_z*s_f, s_orientation='left' )
    
    lib.join( objects_to_join=[ "body_top", "body_front_right", "body_front_left", "body_back_right", "body_back_left", 'thruster_front_right','thruster_front_left', "thruster_back_right", "thruster_back_left"])
    #create_weld_modifier( root = body_top, name='w1', apply=True )
    lib.create_remesh( bpy.context.view_layer.objects.active, name='remesh1', octree_depth=4, apply=True )

    lib.create_bevel_modifier( root = body_top, name="b8", segments=1, width=.8, apply=True )
    


# main script
lib.clear_scene()
create_ship()
#create_container()

