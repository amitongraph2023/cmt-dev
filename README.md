CMT LOCAL SESSION

UI 

to run locally, for the API, you need to comment out the cookie.setSecure(isSecure) line on line 59 of the SharedUtils folder. 
It's in the src/main/com.panera.cmt/util folder


1.UI folder : npm install
2.if needed run npm install -g @angular/cli
3. ng serve --host 0.0.0.0 --port 3000 --disable-host-check
4. in cmt folder go to bash script and ./securesession.sh 
5.chroe opens and give login 
user :cmtadmin
pasword : Bread1234



BACKEND :

MVN spring-boot:run 
backend runs on 8080 in dev .Not needed to login for most of the UI work.
