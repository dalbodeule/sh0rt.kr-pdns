#!/usr/bin/env bash
set -euo pipefail

usage() {
  echo "Usage: $0 <image-tag>" >&2
  echo "Set IMAGE_REPOSITORY to override the default repository." >&2
  exit 2
}

if [[ $# -ne 1 ]]; then
  usage
fi

tag=$1
if [[ ! $tag =~ ^[A-Za-z0-9_][A-Za-z0-9_.-]{0,121}$ ]]; then
  echo "Invalid Docker image tag: $tag" >&2
  exit 2
fi

repository=${IMAGE_REPOSITORY:-${DOCKER_USERNAME:-dalbodeule}/dnsapi}
publish_latest=${PUBLISH_LATEST:-1}
dry_run=${DRY_RUN:-0}
resume=${RESUME:-0}
root_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)

if [[ $publish_latest != 0 && $publish_latest != 1 ]]; then
  echo "PUBLISH_LATEST must be 0 or 1" >&2
  exit 2
fi
if [[ $dry_run != 0 && $dry_run != 1 ]]; then
  echo "DRY_RUN must be 0 or 1" >&2
  exit 2
fi
if [[ $resume != 0 && $resume != 1 ]]; then
  echo "RESUME must be 0 or 1" >&2
  exit 2
fi
if [[ $dry_run == 0 ]]; then
  command -v docker >/dev/null || { echo "Docker is required" >&2; exit 1; }
  docker buildx version >/dev/null
fi

run() {
  if [[ $dry_run == 1 ]]; then
    printf 'DRY RUN:'
    printf ' %q' "$@"
    printf '\n'
  else
    "$@"
  fi
}

cd "$root_dir"
for arch in amd64 arm64; do
  image="$repository:$tag-$arch"
  if [[ $resume == 1 && $dry_run == 0 ]] && docker image inspect "$image" >/dev/null 2>&1; then
    actual_platform=$(docker image inspect "$image" --format '{{.Os}}/{{.Architecture}}')
    if [[ $actual_platform != "linux/$arch" ]]; then
      echo "$image has platform $actual_platform, expected linux/$arch" >&2
      exit 1
    fi
    echo "Reusing local image $image"
  else
    run ./gradlew bootBuildImage \
      -PmultiArchImage=true \
      "--imagePlatform=linux/$arch" \
      "--imageName=$image" \
      --no-publishImage
  fi
  run docker push "$image"
done

manifest_args=(docker buildx imagetools create --tag "$repository:$tag")
if [[ $publish_latest == 1 ]]; then
  manifest_args+=(--tag "$repository:latest")
fi
manifest_args+=("$repository:$tag-amd64" "$repository:$tag-arm64")
run "${manifest_args[@]}"
run docker buildx imagetools inspect "$repository:$tag"
