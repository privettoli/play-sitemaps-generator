#!/usr/bin/env bash
apt-get update
apt-get install -y python-software-properties curl

# Java repo
add-apt-repository ppa:webupd8team/java
echo "\n Java repo installed! \n"

# MariaDb repo
apt-get install software-properties-common
apt-key adv --recv-keys --keyserver hkp://keyserver.ubuntu.com:80 0xcbcb082a1bb943db
add-apt-repository 'deb http://ftp.nluug.nl/db/mariadb/repo/10.0/ubuntu precise main'
echo "\n MariaDb repo installed! \n"

# Download and import Scala
apt-get remove scala-library scala
wget http://www.scala-lang.org/files/archive/scala-2.11.6.deb
dpkg -i scala-2.11.6.deb
echo "\n Scala downloaded and imported! \n"

# # Import SBT
wget http://dl.bintray.com/sbt/debian/sbt-0.13.8.deb
dpkg -i sbt-0.13.8.deb 
echo "\n SBT downloaded and imported! \n"

# Update dependencies
apt-get update
echo "\n Dependencies updated! \n"
apt-get -f install -y
echo "\n Apt-get -f install finished! \n"

# Accept Oracle's licence
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
# Install Java
apt-get install -y oracle-java8-installer
apt-get install -y oracle-java8-set-default

# Install Scala
apt-get install -y scala

# Install SBT
apt-get install -y sbt

# Install ruby for sass
apt-get install -y ruby-full

# Install sass
gem install sass

# Install MariaDb
debconf-set-selections <<< "mariadb-server-10.0 mysql-server/root_password password pass"
debconf-set-selections <<< "mariadb-server-10.0 mysql-server/root_password_again password pass"
apt-get -y install mariadb-server
mysql -uroot -p"pass" -e "CREATE DATABASE site_maps_generator DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;"

# Compile source code
cd /vagrant
sbt stage

# Run server
./target/universal/stage/bin/sitemaps