package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import it.polito.tdp.yelp.db.YelpDao;

public class Model {

	private YelpDao dao;
	private Graph<Business, DefaultEdge> graph;
	private Business b1;
	private List<Business> businesses;
	private List<Business> res = new ArrayList<>();
	private double km = 0;

	public Model() {
		this.dao = new YelpDao();
	}
	
	public List<Business> creaGrafo(String city) {
		this.graph = new SimpleWeightedGraph<>(DefaultEdge.class);
		
		businesses = this.dao.getBusinessCity(city);
		Graphs.addAllVertices(this.graph, businesses);
		for(Business b1: businesses) {
			for(Business b2: businesses) {
				if(!b1.equals(b2)) {
					DefaultEdge edge = this.graph.getEdge(b1, b2);
					if(edge == null) {
						double d = LatLngTool.distance(b1.getPoint(), b2.getPoint(), LengthUnit.KILOMETER);
						Graphs.addEdge(this.graph, b1, b2, d);
					}
				}
			}
		}
		System.out.println(this.graph);
		return businesses;
	}
	
	public List<String> getAllCities() {
		return this.dao.getAllCities();
	}

	public double trovaLocaleDistante(Business business) {
		List<Business> near = Graphs.neighborListOf(this.graph, business);
		double distanza = 0;
		
		for(Business b: near) {
			DefaultEdge e = this.graph.getEdge(business, b);
			double d = this.graph.getEdgeWeight(e);
			if(d>distanza) {
				distanza = d;
				this.b1 = b;
			}
		}
		
		return distanza;
	}
	
	public List<Business> calcolaPercorso(Business start, Business end, double x) {
		res = new ArrayList<Business>();
		List<Business> parziale = new ArrayList<>();
		parziale.add(start);
		
		this.ricorsiva(parziale, end, x, 0);
		
		return this.res;
	}

	private void ricorsiva(List<Business> parziale, Business end, double x, double km) {
		if(parziale.get(parziale.size()-1).equals(end)) {
			if(parziale.size() > res.size()) {
				res = new ArrayList<Business>(parziale);
				this.km = km;
			}
		} else {
			for(Business b: this.businesses) {
				if(!parziale.contains(b)) {
					if(b.getStars() >= x || b.equals(end)) {
						
						DefaultEdge e = this.graph.getEdge(b, parziale.get(parziale.size()-1));
						double k = graph.getEdgeWeight(e);
						
						parziale.add(b);
						k += km;
						
						this.ricorsiva(parziale, end, x, k);
				
						k -= km;
						parziale.remove(b);
					}
				}
			}
		}
	}
	
	public Business getB1() {
		return this.b1;
	}

	public Graph<Business, DefaultEdge> getGraph() {
		return graph;
	}

	public double getKm() {
		return km;
	}
	
}
