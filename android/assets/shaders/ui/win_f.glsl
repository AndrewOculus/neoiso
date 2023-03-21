#ifdef GL_ES
#define LOWP lowp
    precision highp float;
#else
    #define LOWP
#endif

varying LOWP vec4 vColor;
varying vec2 vTexCoord0;

uniform sampler2D u_texture;
uniform sampler2D map_texture;

//uniform float x = 5;
//uniform float y = 5;
//vec2 c = vec2(0.5, 0.5);
//float stp = 0.003;
//int n = 50;

float len(vec2 v){
    return sqrt(v.x*v.x + v.y*v.y);
}

void main()
{
    vec4 tex = texture2D(map_texture, vec2(1.0) - vTexCoord0*0.3);
//
//    if( tex.a == 0.0){
//        for( int i = 0 ; i < n ; i++){
//            vec2 forw = vec2( x * stp * float(i) , y * stp * float(i) );
//            vec2 npos = forw + vTexCoord0;
//            vec4 ncol = texture2D(u_texture, npos);
//
//            if(ncol.a == 1.0)
//            {
//                ncol.a = (float(n) - float(i))/float(n);
//                ncol.g = 1.0 - ncol.a;
//                ncol.b = 1.0 - ncol.a;
//                tex = ncol;
//                break;
//            }
//        }
//
//    }
    gl_FragColor = vColor * tex;
}
