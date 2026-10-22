#!/usr/bin/env bash
# Prints signatures of the game / Fabric API classes the mod touches, to debug porting.
set -u
jars=$(find ~/.gradle/caches .gradle -name '*.jar' 2>/dev/null | grep -v sources)
cp_list=""
for j in $jars; do cp_list="$cp_list:$j"; done
find_class() { # simple name -> FQCN list
  for j in $jars; do unzip -Z1 "$j" 2>/dev/null | grep -E "(^|/)$1\.class$"; done | sort -u | sed 's/\.class$//; s#/#.#g'
}
for name in "$@"; do
  for fq in $(find_class "$name"); do
    echo "===== $fq"
    javap -p -cp "$cp_list" "$fq" 2>&1 | grep -vE 'lambda\$' | head -400
  done
done
