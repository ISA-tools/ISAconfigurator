mvn clean assembly:assembly

get_tag_data () {
    local tag=$1
    local xml_file=$2

    # Find tag in the xml, convert tabs to spaces, remove leading spaces, remove the tag.
    grep $tag $xml_file | \
        tr '\011' '\040' | \
        sed -e 's/^[ ]*//' \
            -e 's/^<.*>\([^<].*\)<.*>$/\1/'

}

ALLVERSIONS=`get_tag_data \<version\> pom.xml  2>/dev/null`

VERSIONSPLIT=(`echo $ALLVERSIONS | tr ' ' ' '`)
# get first version number referenced. Should always be the version of the project, since it's always the first
VERSION=${VERSIONSPLIT[0]}

if [ "$VERSION" = "" ] ; then 
  echo "Couldn't extract version from pom.xml. Exiting."
fi


echo "Replacing JavaApplicationStub with Symbolic link to users Stub."
cd target
rm -r ISACreatorConfigurator-$VERSION.app/Contents/MacOS/JavaApplicationStub


# this will obviously only work on the MAC. If you are using another operating system, you should download from another system
ln -s /System/Library/Frameworks/JavaVM.framework/Resources/MacOS/JavaApplicationStub ISACreatorConfigurator-$VERSION.app/Contents/MacOS/JavaApplicationStub

echo "Packaging completed successfully!"



