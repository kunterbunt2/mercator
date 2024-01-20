vec3 packFloatToVec3(float value, float minValue, float maxValue)
{
    float packScale = maxValue - minValue;
    float scale = (value - minValue) * (256.0 * 256.0 * 256.0 - 1.0) / (256.0 * 256.0 * 256.0) / packScale;
    vec4 encode = fract(scale * vec4(1.0, 256.0, 256.0 * 256.0, 256.0 * 256.0 * 256.0));
    return encode.xyz - encode.yzw / 256.0 + 1.0/512.0;
}
