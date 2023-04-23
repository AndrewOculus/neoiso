#ifdef GL_ES   
    #define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;
// sampler2D это специальный формат данных в  glsl для доступа к текстуре
uniform sampler2D u_texture;

uniform float time;

void main(){
    vec2 uv = vec2( 0.0025 * sin(time + v_texCoords.x*30.0)  - 0.0005 * cos(time* 4.0 + v_texCoords.y*30.0) + v_texCoords.x, 0.0025 * cos(time* 2.0 + v_texCoords.y*30.0) - 0.0005 * sin(time* 3.0 + v_texCoords.y*30.0) + v_texCoords.y );
    gl_FragColor = v_color * texture2D(u_texture, uv);    
}