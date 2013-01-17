__author__ = 'eamonnmaguire'

import os

# first create the app, should externalise some names to make it a bit nicer
print 'Packaging up jars to run within a native MacOS container'
os.makedirs('ISAcreatorConfigurator.app/Contents/Resources/Java')
os.makedirs('ISAcreatorConfigurator.app/Contents/MacOS')


os.chdir('ISAcreatorConfigurator.app/Contents/')
f = open('Info.plist', 'w')
f.write('<?xml version="1.0" encoding="UTF-8"?>')
f.write('<dict>'\
        '<key>CFBundleName</key>'\
        '<string>ISAcreator Configurator</string>'\
        '<key>CFBundleIdentifier</key>'\
        '<string>org.isatools.isacreatorconfigurator.configui.ISAcreatorConfigurator</string>'\
        '<key>CFBundleVersion</key>'\
        '<string>1.6</string>'\
        '<key>CFBundleAllowMixedLocalizations</key>'\
        '<string>true</string>'\
        '<key>CFBundleExecutable</key>'\
        '<string>JavaApplicationStub</string>'\
        '<key>CFBundleDevelopmentRegion</key>'\
        '<string>English</string>'\
        '<key>CFBundlePackageType</key>'\
        '<string>APPL</string>'\
        '<key>CFBundleShortVersionString</key>'\
        '<string>1.6</string>'\
        '<key>CFBundleSignature</key>'\
        '<string>????</string>'\
        '<key>CFBundleGetInfoString</key>'\
        '<string>ISAcreatorConfigurator</string>'\
        '<key>CFBundleInfoDictionaryVersion</key>'\
        '<string>6.0</string>'\
        '<key>CFBundleIconFile</key>'\
        '<string>isaconfigurator-icon.icns</string>'\
        '<key>Java</key>'\
        '<dict>'\
        '<key>VMOptions</key>'\
        '<string>-Xms256mm -Xmx1024mm</string>'\
        '<key>MainClass</key>'\
        '<string>org.isatools.isacreatorconfigurator.configui.ISAcreatorConfigurator</string>'\
        '<key>JVMVersion</key>'\
        '<string>1.5+</string>'\
        '<key>ClassPath</key>'\
        '<string>$JAVAROOT/ISACreatorConfigurator.jar</string>'\
        '</dict>'\
        '</dict>')
f.close()

os.chdir('../../')

os.system("mv %s %s" % ('ISACreatorConfigurator-1.6-jar-with-dependencies.jar', 'ISACreatorConfigurator.jar'))
os.system("cp %s %s" % ('ISACreatorConfigurator.jar', 'ISAcreatorConfigurator.app/Contents/Resources/Java/'))
os.system("cp %s %s" % ('../src/main/resources/images/icon/isaconfigurator-icon.icns', 'ISAcreatorConfigurator.app/Contents/Resources/'))
os.system(
    "ln -s /System/Library/Frameworks/JavaVM.framework/Resources/MacOS/JavaApplicationStub ISAcreatorConfigurator.app/Contents/MacOS/JavaApplicationStub")
os.system("chmod -R 755 ISACreatorConfigurator.app")


# then create a disk image (dmg)
