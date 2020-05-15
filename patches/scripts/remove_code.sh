perl -i -0pe 's/<script type="text\/javascript" src="js\/ga.js"><\/script>//g' saiku-ui/index.html
rm -f saiku-ui/js/ga.js
echo 'removed google analytics'

perl -i -0pe 's/<script.*?id=\"template-upgrade\">(.|\n)*?<\/script>//g' saiku-ui/index.html
echo 'removed saiku advertisement'

perl -i -0pe 's/<script type=\"text\/javascript\" src=\"js\/logger\/janky.post.min.js\"><\/script>//g' saiku-ui/index.html
rm -f saiku-ui/js/logger/janky.post.min.js
echo 'removed janky post logger'
