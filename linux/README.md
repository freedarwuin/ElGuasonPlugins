# Assumptions
Know how to use Lutris.

# Instructions
Import the local yaml into Lutris, it should do everything for you.
**BUT:**
* Make sure to have the correct jdk installed. The current runner fully qualifies eclipse temurin's jdk on Fedora.
## Install jdk
### Fedora:
(Taken from https://adoptium.net/installation/linux/)
<code># Uncomment and change the distribution name if you are not using CentOS/RHEL/Fedora
# DISTRIBUTION_NAME=centos

cat <<EOF > /etc/yum.repos.d/adoptium.repo
[Adoptium]
name=Adoptium
baseurl=https://packages.adoptium.net/artifactory/rpm/${DISTRIBUTION_NAME:-$(. /etc/os-release; echo $ID)}/\$releasever/\$basearch
enabled=1
gpgcheck=1
gpgkey=https://packages.adoptium.net/artifactory/api/gpg/key/public
EOF</code>
<code># yum install temurin-11-jdk</code></br>
And the current runner's java bin path will work!</br>
### Gentoo:
<code># emerge --ask dev-java/openjdk-bin:11</code>
<code>$ eselect java-vm list</code>
<code># eselect java-vm set system openjdk-bin-11-my_number</code>
Java path for runner: <code>/usr/lib/nvm/openjdk-bin-11/bin/java</code>
(note: Gentoo main repo bin is compiled by eclipse temurin)<br>

# Help
Unless you have a wonky setup, the path for the classes should work.
If it doesn't, run <code>lutris -d</code> with debug enabled. If the class path can't be loaded, it's a FULLY qualified path. Make sure the path matches the lutris install directory for the wine prefix.
From experience, if the problem isn't with lutris, it's from paths. Fully qualified paths will reduce errors and likely be the fix. If the class path isn't loading, find your paths and use fully qualified paths.

# Credit
Lutris yaml has been heavily borrowed from [TormStorm's jagex-launcher-linux](https://github.com/TormStorm/jagex-launcher-linux). I've repurposed it to work with EthanApi and PiggyPlugins.
