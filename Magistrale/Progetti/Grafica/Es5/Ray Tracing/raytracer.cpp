#include "raytracer.h"
#include "material.h"
#include "vectors.h"
#include "argparser.h"
#include "raytree.h"
#include "utils.h"
#include "mesh.h"
#include "face.h"
#include "sphere.h"

// casts a single ray through the scene geometry and finds the closest hit
// Fa l'intersezione del raggio in ingresso e restituisce il punto eventualmente di intersezione
bool
RayTracer::CastRay (Ray & ray, Hit & h, bool use_sphere_patches) const
{
  bool answer = false;
  Hit nearest;
  nearest = Hit();

  // intersect each of the quads
  for (int i = 0; i < mesh->numQuadFaces (); i++)
  {
	Face *f = mesh->getFace (i);
	if (f->intersect (ray, h, args->intersect_backfacing))
	{
		if( h.getT() < nearest.getT() )
		{
			answer = true;
			nearest = h;
		}
	}
  }

  // intersect each of the spheres (either the patches, or the original spheres)
  if (use_sphere_patches)
  {
	for (int i = mesh->numQuadFaces (); i < mesh->numFaces (); i++)
	{
	  Face *f = mesh->getFace (i);
	  if (f->intersect (ray, h, args->intersect_backfacing))
	  {
		if( h.getT() < nearest.getT() )
		{
			answer = true;
			nearest = h;
		}
	  }
	}
  }
  else
  {
	const vector < Sphere * >&spheres = mesh->getSpheres ();
	for (unsigned int i = 0; i < spheres.size (); i++)
	{
	  if (spheres[i]->intersect (ray, h))
	  {
		if( h.getT() < nearest.getT() )
		{
			answer = true;
			nearest = h;
		}
	  }
	}
  }

  h = nearest;
  return answer;
}

// Il punto pixel genera un raggio (da pixel da punto di vista: raggio primario), hit ? il punto di intersezione
Vec3f
RayTracer::TraceRay (Ray & ray, Hit & hit, int bounce_count) const {

  hit = Hit ();
  bool intersect = CastRay (ray, hit, false);

  if( bounce_count == args->num_bounces )
  	RayTree::SetMainSegment (ray, 0, hit.getT () );
  else
	RayTree::AddReflectedSegment(ray, 0, hit.getT() );

  Vec3f answer = args->background_color;

  Material *m = hit.getMaterial ();
  if (intersect == true) {
	assert (m != NULL);
	Vec3f normal = hit.getNormal ();
	Vec3f point = ray.pointAtParameter (hit.getT ());

	// ----------------------------------------------
	// ambient light
	answer = args->ambient_light * m->getDiffuseColor ();


	// ----------------------------------------------
	// if the surface is shiny...
	Vec3f reflectiveColor = m->getReflectiveColor ();

	// ==========================================
	// ASSIGNMENT:  ADD REFLECTIVE LOGIC
	// ==========================================

	if (reflectiveColor.Length() != 0 && bounce_count > 0) {
		//  ****************** Ricorsione del ray-tracing ******************
		Vec3f rayVector = ray.getDirection();
		Vec3f reflection = rayVector - (2 * rayVector.Dot3(normal) * normal);
		reflection.Normalize();
		Ray* new_ray = new Ray(point, reflection);

		answer += TraceRay(*new_ray, hit, bounce_count - 1) * reflectiveColor;
		// *****************************************************************
	} 

	// ----------------------------------------------
	// add each light (Phong)
	int num_lights = mesh->getLights ().size ();
	for (int i = 0; i < num_lights; i++) {
	    if(!softShadow) {
            // ==========================================
            // ASSIGNMENT:  ADD HARD SHADOW LOGIC
            // ==========================================
            Face *f = mesh->getLights ()[i]; // Light object

            // Hard shadow
            Vec3f pointOnLight = f->computeCentroid ();
            Vec3f dirToLight = pointOnLight - point; // l: shadow ray
            dirToLight.Normalize();

            Ray* n_ray = new Ray(point, dirToLight);
            Hit* new_hit = new Hit();
            bool colpito = CastRay(*n_ray, *new_hit, false);
            Vec3f dist;

            if (colpito) {
                Vec3f n_point = n_ray->pointAtParameter(new_hit->getT());
                // Calcola il vettore distanza fra il punto colpito dal raggio e il punto sulla luce
                dist.Sub(dist, n_point, pointOnLight);

                if (dist.Length() < 0.01) { // Il punto colpito coincide con la luce
                    if (normal.Dot3(dirToLight) > 0) {
                        Vec3f lightColor = 0.2 * f->getMaterial()->getEmittedColor() * f->getArea();
                        answer += m->Shade(ray, hit, dirToLight, lightColor, args);
                    }
                }
            }

            if (normal.Dot3 (dirToLight) > 0) {
                Vec3f lightColor = 0.2 * f->getMaterial ()->getEmittedColor () * f->getArea ();
                answer += m->Shade (ray, hit, dirToLight, lightColor, args);
            }
	    } else {
            // ==========================================
            // OPTIONAL:  ADD SOFT SHADOW LOGIC
            // ==========================================
            Face *f = mesh->getLights ()[i]; // Light object
            Vec3f pointOnLight, dirToLight, tempAns;
            int numShadowRays = 500;

            // Soft shadow
            for (int j = 0; j < numShadowRays; ++j) {
                pointOnLight = f->RandomPoint (); // Random source from the current light
                dirToLight = pointOnLight - point; // l: shadow ray
                dirToLight.Normalize ();

                Ray* n_ray = new Ray(point, dirToLight);
                Hit* new_hit = new Hit();
                bool colpito = CastRay(*n_ray, *new_hit, false);
                Vec3f dist;

                if (colpito) {
                    Vec3f n_point = n_ray->pointAtParameter(new_hit->getT());
                    // Calcola il vettore distanza fra il punto colpito dal raggio e il punto sulla luce
                    dist.Sub(dist, n_point, pointOnLight);

                    if (dist.Length() < 0.1) { // Il punto colpito coincide con la luce
                        if (normal.Dot3(dirToLight) >= 0) {
                            Vec3f lightColor = 0.8 * normal.Dot3(dirToLight) * f->getMaterial()->getEmittedColor() * f->getArea();
                            Vec3f ans = m->Shade(ray, hit, dirToLight, lightColor, args);
                            ans.Divide(numShadowRays, numShadowRays, numShadowRays);
                            answer += ans;
                        }
                    }
                }
            }
	    }
	}
    
  }

  return answer;
}
