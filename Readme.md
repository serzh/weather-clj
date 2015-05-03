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

# Development
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