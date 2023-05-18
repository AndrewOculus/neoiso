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

    //float uv_x = 0.0055 * abs(sin(time * 4.0 + 5.0 * (v_texCoords.x - 0.5))) + v_texCoords.x;
    //float uv_y = 0.0055 * abs(sin(time * 4.0 + 5.0 * (v_texCoords.y - 0.5))) + v_texCoords.y;
    //vec2 uv = vec2( uv_x, uv_y );
    vec2 uv = vec2( 0.0055 * sin(time + (v_texCoords.x - 0.5)*30.0)  - 0.0005 * cos(time* 4.0 + v_texCoords.y*30.0) + v_texCoords.x, 0.0055 * cos(time* 2.0 + v_texCoords.y*30.0) - 0.0005 * sin(time* 3.0 + v_texCoords.y*30.0) + v_texCoords.y );
    vec4 col = vec4( v_color.r  - 0.025 * sin(time*5.0) , v_color.g - 0.01 * sin(18.0 * time) + 0.015 * sin(time*3.0), v_color.b - 0.017 * sin(time*8.6 )  + 0.017 * cos(time*8.6 ), 1.0);
    vec4 wave = col * texture2D(u_texture, uv);
    if( wave.a < 0.2 ){
        gl_FragColor = col * texture2D(u_texture, v_texCoords); 
    }else{
        gl_FragColor = wave;
    }
}