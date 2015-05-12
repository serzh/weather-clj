# Weather CLJ
Application that allows you to view weather information
for Tampere, London, Durham NC for any date.

# Run
To run this project you should have installed leiningen and jdk.

Copy profiles.clj file from template

    cp profiles.example.clj profiles.clj
    
Edit profiles.clj for your needs to configure app.
For example:

    {:dev-env {:env {:web-host "0.0.0.0"
                     :web-port 8888}}}
                     
And run the application

    lein run
    
# Deployment
To deploy application you should have access to the machines in deploy/production.hosts
by ssh key without password. You can do this by running command

    ssh-copy-id weather@machines-ip
    
You can ask me about the weather user password.

After that you have to run following command in the deploy dir

    ansible-playbook -i production.hosts site.yml
    
*Important*. This playbooks was designed to deploy to the Ubuntu boxes.

To update existing version of the application run

    ansible-playbook -i production.hosts update_weather_app.yml

# Development

## Database
This application uses *postgresql* as it's primary database.
You can use your own cluster, previously run deploy/postgresql/files/schema.sql on it.
Or, preferable, you can run it in Vagrant using [Ansible](http://docs.ansible.com/index.html). You should install
[vagrant](http://www.vagrantup.com/downloads) and [virtualbox](https://www.virtualbox.org/wiki/Linux_Downloads).
Then you should setup vagrant environment.

    cd $PROJECT_DIR
    vagrant up

This will setup *db* virtual machine with 192.168.0.2 ip.
Then run ansible to setup postgres on the vm and sit back

    cd $PROJECT_DIR/deploy
    ansible-playbook -i local.hosts pre_postgresql.yml
    
## Application

Configure profiles.clj file as described in Run topic.
                     
This project uses sierra's component library.
To run repl and start system do

    lein repl
    # in repl
    => (user/dev) ;; load all namespaces
    => (go)       ;; start system
    # misc
    => (reset)    ;; restart system
    => (stop)     ;; stop system
    => (init)     ;; create new not started system
    => (start)    ;; start system
    => (go)       ;; shorthand for (do (init) (start))
    
To run figwheel repl do

    lein do clean, figwheel
    
end follow http://localhost:3449 at the browser.