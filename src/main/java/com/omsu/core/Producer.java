package com.omsu.core;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by dkuzmin on 7/6/2016.
 */
@XmlRootElement
public class Producer {

    private Integer id;

    private String name;

    private String description;

    public Integer getId() {
        return id;
    }

    @XmlAttribute
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @XmlElement
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    @XmlElement
    public void setDescription(String description) {
        this.description = description;
    }

    public Producer copyTo(){
        Producer newProducer = new Producer();
        newProducer.setId(this.id);
        newProducer.setName(this.name);
        newProducer.setDescription(this.description);
        return newProducer;
    }

    @Override
    public String toString() {
        return "Producer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
