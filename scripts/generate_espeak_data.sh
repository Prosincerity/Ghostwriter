#!/usr/bin/env bash
set -euo pipefail

project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
source_root="$project_root/third_party/espeak-ng"
build_root="${1:-$project_root/build/espeak-host}"
asset_root="$project_root/app/src/main/assets/espeak-ng-1.52/espeak-ng-data"

cmake -S "$source_root" -B "$build_root" -G Ninja \
  -DCMAKE_POLICY_VERSION_MINIMUM=3.5 \
  -DUSE_MBROLA=OFF -DUSE_LIBSONIC=OFF -DUSE_LIBPCAUDIO=OFF \
  -DUSE_KLATT=OFF -DUSE_SPEECHPLAYER=OFF -DUSE_ASYNC=OFF \
  -DSONIC_LIB=disabled -DSONIC_INC="$source_root"
cmake --build "$build_root" --target espeak-ng-bin
ninja -C "$build_root" \
  espeak-ng-data/en_dict espeak-ng-data/de_dict espeak-ng-data/tr_dict

for file in intonations phondata phonindex phontab en_dict de_dict tr_dict \
  lang/gmw/en lang/gmw/de lang/trk/tr; do
  install -Dm644 "$build_root/espeak-ng-data/$file" "$asset_root/$file"
done

cli="$build_root/src/espeak-ng"
test "$(ESPEAK_DATA_PATH="$asset_root/.." "$cli" -q --ipa -v en ghostwriter)" = 'ɡˈəʊstɹaɪtə'
test "$(ESPEAK_DATA_PATH="$asset_root/.." "$cli" -q --ipa -v de Übermut)" = 'ˌyːbɜmˈuːt'
test "$(ESPEAK_DATA_PATH="$asset_root/.." "$cli" -q --ipa -v tr ışık)" = 'ɯʃˈɯk'
