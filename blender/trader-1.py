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

def create_container( s_x=0, s_y=0, s_z=0, s_f=8 ):
    for cx in range(0, 3):
        bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(x, y-4*s_f+cx*s_f, z-1*s_f), scale=(1*s_f, 1*s_f, 1*s_f))
        obj = bpy.context.active_object
        obj.name = 'container-'+str(cx)
        mat = create_material(m_name="m.container.plane",m_color=(0.5, 0.5, 1.0, 1.0),m_metallic=0.7,m_roughness=0.2)
        # No Specular input found, skipping it.
        obj.data.materials.append(mat)


def create_thruster( s_name='thruster', s_x=0, s_y=0, s_z=0, s_f=8, s_orientation='right' ):
    if s_orientation == 'right':
        dx = 1
    else:
        dx = -1
    
    bpy.ops.mesh.primitive_uv_sphere_add(radius=0.3*s_f, enter_editmode=False, align='WORLD', location=(s_x, s_y, s_z+0.5*s_f), scale=(1, 1, 1))
    ball_top = bpy.context.active_object
    ball_top.hide_set(True)

    bpy.ops.mesh.primitive_uv_sphere_add(radius=0.3*s_f, enter_editmode=False, align='WORLD', location=(s_x, s_y+0.5*s_f, s_z), scale=(1, 1, 1))
    ball_front = bpy.context.active_object
    ball_front.hide_set(True)

    bpy.ops.mesh.primitive_uv_sphere_add(radius=0.3*s_f, enter_editmode=False, align='WORLD', location=(s_x, s_y-0.5*s_f, s_z), scale=(1, 1, 1))
    ball_back = bpy.context.active_object
    ball_back.hide_set(True)

    bpy.ops.mesh.primitive_uv_sphere_add(radius=0.3*s_f, enter_editmode=False, align='WORLD', location=(s_x, s_y, s_z-0.5*s_f), scale=(1, 1, 1))
    ball_bottom = bpy.context.active_object
    ball_bottom.hide_set(True)

    bpy.ops.mesh.primitive_uv_sphere_add(radius=0.3*s_f, enter_editmode=False, align='WORLD', location=(s_x+dx*0.5*s_f, s_y, s_z), scale=(1, 1, 1))
    ball_x = bpy.context.active_object
    ball_x.hide_set(True)

    thruster_mat = lib.create_material(m_name="m.thruster",m_color=(1, 1, 1, 1.0),m_metallic=0.5,m_roughness=0.5)

    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(s_x, s_y, s_z), scale=(1*s_f, 1*s_f, 1*s_f))
    thruster = bpy.context.active_object
    thruster.name = 'thruster'
    thruster.data.materials.append(thruster_mat)

    m1 = thruster.modifiers.new(name="b1", type='BOOLEAN')
    m1.operation = 'DIFFERENCE'
    m1.object = ball_top
    bpy.context.view_layer.objects.active = thruster
    bpy.ops.object.modifier_apply(modifier='b1')

    m2 = thruster.modifiers.new(name="b2", type='BOOLEAN')
    m2.operation = 'DIFFERENCE'
    m2.object = ball_front
    bpy.context.view_layer.objects.active = thruster
    bpy.ops.object.modifier_apply(modifier='b2')

    m3 = thruster.modifiers.new(name="b3", type='BOOLEAN')
    m3.operation = 'DIFFERENCE'
    m3.object = ball_back
    bpy.context.view_layer.objects.active = thruster
    bpy.ops.object.modifier_apply(modifier='b3')

    m4 = thruster.modifiers.new(name="b4", type='BOOLEAN')
    m4.operation = 'DIFFERENCE'
    m4.object = ball_bottom
    bpy.context.view_layer.objects.active = thruster
    bpy.ops.object.modifier_apply(modifier='b4')

    m5 = thruster.modifiers.new(name="b5", type='BOOLEAN')
    m5.operation = 'DIFFERENCE'
    m5.object = ball_x
    bpy.context.view_layer.objects.active = thruster
    bpy.ops.object.modifier_apply(modifier='b5')

    #create_bevel_modifier( m_root = thruster, m_name="b6", m_segments=3, m_width_pct=0.4 )

def create_ship( s_x=0, s_y=0, s_z=0, s_f=8 ):

    body_mat = lib.create_material(m_name="m.body",m_color=lib.hex_to_rgba("#FFA500FF"),m_metallic=0.1,m_roughness=.5)
    # body-top
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(s_x, s_y, s_z), scale=(1*s_f, 11*s_f, 1*s_f))
    body_top = bpy.context.active_object
    body_top.name = 'body_top'
    body_top.data.materials.append(body_mat)

    # body-cockpit
    pilot_head_mat = lib.create_material(m_name="m.pilot.head",m_color=(1,1,1,1),m_metallic=0.5,m_roughness=0.5)
    bpy.ops.mesh.primitive_uv_sphere_add(radius=0.2*s_f, enter_editmode=False, align='WORLD', location=(s_x, s_y+6*s_f+0.1, s_z+0.1), scale=(1, 1, 1))
    pilot_head = bpy.context.active_object
    pilot_head.data.materials.append(pilot_head_mat)


    cockpit_mat = lib.create_material(m_name="m.cockpit",m_color=(0, 0, 0, 1),m_metallic=1,m_roughness=0.1,m_alpha=0.8)
    bpy.ops.mesh.primitive_cube_add(size=1, enter_editmode=False, align='WORLD', location=(s_x, s_y+6*s_f+0.1, s_z+0.1), scale=(1*s_f, 1*s_f, 1*s_f))
    body_cockpit = bpy.context.active_object
    body_cockpit.name = 'body_cockpit'
    body_cockpit.data.materials.append(cockpit_mat)
    lib.create_bevel_modifier( m_root = body_cockpit, m_name="b8", m_segments=3, m_width_pct=.4 )

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
    
#    join_remesh( objects_to_join=["body_top"/*, "body_back", "body_front"*/,"body_front_right","body_front_left","body_back_right","body_back_left","thruster_back_right", "thruster_back_left"])
    lib.join_remesh( objects_to_join=["body_top","body_front_right","body_front_left","body_back_right","body_back_left","thruster_back_right", "thruster_back_left"])

#    m = m_root.modifiers.new(name=m_name, type='BEVEL')
#    m.offset_type = 'ABSOLUTE'
#    m.width = m_width_pct
#    m.segments = m_segments
#    m.angle_limit = 0.523599
#    m_root.select_set(True)
#    bpy.context.view_layer.objects.active = m_root
#    bpy.ops.object.modifier_apply(modifier=m.name)
    
#    create_boolean_modifier( m_root = body_top, m_name="b1", m_operation = 'UNION', m_object = body_front )
#    create_boolean_modifier( m_root = body_top, m_name="b2", m_operation = 'UNION', m_object = body_back )
#    create_boolean_modifier( m_root = body_top, m_name="b3", m_operation = 'UNION', m_object = body_front_right )
#    create_boolean_modifier( m_root = body_top, m_name="b4", m_operation = 'UNION', m_object = body_front_left )
#    create_boolean_modifier( m_root = body_top, m_name="b5", m_operation = 'UNION', m_object = body_back_right )
#    create_boolean_modifier( m_root = body_top, m_name="b6", m_operation = 'UNION', m_object = body_back_left )
#    create_boolean_modifier( m_root = body_top, m_name="b7", m_operation = 'UNION', m_object = body_cockpit )
    width_pct = .8
    lib.create_bevel_modifier( m_root = body_top, m_name="b8", m_segments=5, m_width_pct=width_pct )
    
#    create_bevel_modifier( m_root = body_front, m_name="b8", m_segments=3, m_width_pct=width_pct )
#    create_bevel_modifier( m_root = body_back, m_name="b8", m_segments=3, m_width_pct=width_pct )
#    create_bevel_modifier( m_root = body_front_right, m_name="b8", m_segments=3, m_width_pct=width_pct )
#    create_bevel_modifier( m_root = body_front_left, m_name="b8", m_segments=3, m_width_pct=width_pct )
#    create_bevel_modifier( m_root = body_back_right, m_name="b8", m_segments=3, m_width_pct=width_pct )
#    create_bevel_modifier( m_root = body_back_left, m_name="b8", m_segments=3, m_width_pct=width_pct )
#    create_bevel_modifier( m_root = body_cockpit, m_name="b8", m_segments=3, m_width_pct=width_pct )


x=0
y=0
z=0

# main script
lib.clear_scene()
create_ship()
#create_container()





#obj = bpy.context.active_object
#mod = obj.modifiers.new(name="Bevel", type="BEVEL")
#mod.width = 0.02
#mod.segments = 3
#mod.profile = 0.5
#bpy.context.object.modifiers["Bevel"].width = 0.01
#bpy.context.object.modifiers["Bevel"].angle_limit = 0.523599





