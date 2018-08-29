

sudo docker run --name cassandra2 -m 800m -d -e CASSANDRA_START_RPC=true -p 9160:9160 -p 9042:9042 -p 7199:7199 -p 7001:7001 -p 7000:7000  -d cassandra
sudo docker run -d --name es2 -m 800m -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.3.2









conf = new BaseConfiguration()
conf.setProperty("storage.backend", "cassandra")
conf.setProperty("storage.hostname", "127.0.0.1")

janusGraph = JanusGraphFactory.open(conf)

janusGraph.io(graphml()).writeGraph("export.xml")






conf = new BaseConfiguration()
conf.setProperty("storage.backend", "cassandra")
conf.setProperty("storage.hostname", "127.0.0.1")
conf.setProperty("index.search.backend", "elasticsearch")
conf.setProperty("index.search.hostname", "127.0.0.1")

graph = JanusGraphFactory.open(conf)

g = graph.traversal()

graph.io(graphml()).writeGraph("export.xml")


