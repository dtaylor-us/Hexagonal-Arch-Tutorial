#!/bin/bash
user="derektaylor"
repo="hexagonal-arch"
version="0.0.2-SNAPSHOT"
# Define a list of Docker images to push
IMAGES=(
    "${user}/${repo}:user-service-${version}"
    "${user}/${repo}:todo-service-${version}"
    # Add more images here
)

# Loop through each image and push it
for IMAGE in "${IMAGES[@]}"; do
    echo "Pushing $IMAGE..."
    docker push "$IMAGE"

    if [ $? -eq 0 ]; then
        echo "$IMAGE pushed successfully."
    else
        echo "Failed to push $IMAGE."
        exit 1 # Stop the script if an image fails to push
    fi
done

echo "All images have been pushed successfully."
