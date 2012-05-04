#updates the website with README.md file and a fresh typechef.jar file

#checkout fresh (if needed)
mkdir pages
cd pages
git clone -s -b gh-pages .. TypeChef
cd TypeChef
git checkout gh-pages -f
git reset --hard

#deploy
cd ../..
cp README.md pages/TypeChef/_includes/README.md
sbt assembly
cp TypeChef-*.jar pages/TypeChef/deploy


cd pages/TypeChef

#commit
git add *
git commit -a -m "update website with genpages.sh"
git push origin gh-pages

#cleanup
#cd ../..
#rm -r -f pages

