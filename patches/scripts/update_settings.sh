perl -i -0pe 's/MEMBERS_SEARCH_LIMIT: \d+/MEMBERS_SEARCH_LIMIT: 300/g' saiku-ui/js/saiku/Settings.js
echo 'updated members search limit'
