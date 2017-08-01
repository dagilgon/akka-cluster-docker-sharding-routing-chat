# Akka & Docker

from (http://mukis.de/pages/akka-cluster-with-docker-containers/).

# Implementacion Akka Cluster + Docker + Akka Sharding / Akka Router Cluster Aware + Akka http (Rest Api)


----------------------- AKKA CLUSTER DOCKER (LOCAL MODE)-----------------------------

# Cambiar build.sbt para seleccionar el main desaeado (original, sharding, routing aware)
```bash
sudo sbt runSeed
sudo sbt runNode
sudo sbt runNode2
```

----------------------- AKKA CLUSTER DOCKER (CLUSTER MODE)-----------------------------

# ubuntu 14

# (Opcional instalar sbt)
```bash
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update
sudo apt-get -y install sbt
```

# (Opcional instalar docker)
```bash
sudo apt-get -y install curl
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
sudo apt-get update
sudo apt-get -y install docker-ce
sudo docker run hello-world
```

# Bajamos el proyecto
```bash
mkdir workspace_scala
cd workspace_scala
git clone https://github.com/dagilgon/activator-akka-docker.git
cd activator-akka-docker
```
# Contruimos la imagen en cada nodo

```bash
sudo sbt docker:publishLocal
```

# bd-worker1
```bash
sudo docker run -p 2551:2551 --name seed-1 --net host akka-docker:2.3.10 --seed
```
# bd-worker2
```bash
sudo docker run -p 2551:2551 --name seed-2 --net host akka-docker:2.3.10 --seed 192.168.131.31:2551
```
# bd worker3
```bash
sudo docker run -p 2551:2551 --name node-1 --net host akka-docker:2.3.10 192.168.131.31:2551 192.168.131.32:2551
```
# bd worker4
```bash
sudo docker run -p 2551:2551 --name node-2 --net host akka-docker:2.3.10 192.168.131.31:2551 192.168.131.32:2551
```
# bd worker5
```bash
sudo docker run -p 2551:2551 --name node-3 --net host akka-docker:2.3.10 192.168.131.31:2551 192.168.131.32:2551
```
# bd worker6
```bash
sudo docker run -p 2551:2551 --name node-4 --net host akka-docker:2.3.10 192.168.131.31:2551 192.168.131.32:2551
```


----------------------- PERFORMANCE -----------------------------

```bash
cat urls.txt 
http://localhost:8888/chat?from=1&to=paco&payload=hola%20majo
http://localhost:8888/chat?from=2&to=paco&payload=hola%20majo
http://localhost:8888/chat?from=3&to=paco&payload=hola%20majo
http://localhost:8888/chat?from=4&to=paco&payload=hola%20majo
http://localhost:8888/chat?from=5&to=paco&payload=hola%20majo
http://localhost:8888/chat?from=6&to=paco&payload=hola%20majo
http://localhost:8888/chat?from=7&to=paco&payload=hola%20majo
http://localhost:8888/chat?from=8&to=paco&payload=hola%20majo
http://localhost:8888/chat?from=9&to=paco&payload=hola%20majo
http://localhost:8888/chat?from=10&to=paco&payload=hola%20majo
```
# Ejemplo 3 clientes concurrentes / 5000 peticiones

```bash
time cat urls.txt | parallel -j 3 'ab -ql -n 5000 -c 1 -k {}' | grep 'Requests per second'
```


