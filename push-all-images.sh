#!/bin/bash

# Lista de imágenes a subir
images=(
  "omarsaez/config-service:latest"
  "omarsaez/eureka-service:latest"
  "omarsaez/api-gateway:latest"
  "omarsaez/m1-simulation:latest"
  "omarsaez/m2-register:latest"
  "omarsaez/m3-request:latest"
  "omarsaez/m4-evaluation:latest"
  "omarsaez/m5-follow:latest"
  "omarsaez/m6-totalcost:latest"
  "omarsaez/frontend-microsevicio:latest"
)

# Iterar sobre cada imagen y hacer push
for image in "${images[@]}"; do
  echo "Subiendo imagen: $image"
  docker push "$image"
done

echo "Todas las imágenes han sido subidas."
