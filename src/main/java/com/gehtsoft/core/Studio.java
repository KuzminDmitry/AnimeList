package com.gehtsoft.core;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Deamor(test for toto - push) on 14.12.2016.
 */
@XmlRootElement
public class Studio {
    private Integer id;

    private String name;

    private String description;

    public Integer getId(){
        return  id;
    }
    @XmlAttribute
    public void setId(Integer id){
        this.id = id;
    }

    public String getName(){
        return name;
    }
    @XmlAttribute
    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }
    @XmlAttribute
    public void setDescription(String description){
        this.description = description;
    }

    public Studio copyTo(){
        Studio newStudio = new Studio();
        newStudio.setId(this.id);
        newStudio.setName(this.name);
        newStudio.setDescription(this.description);
        return newStudio;

    }
    @Override
    public String toString(){
        return "Studio{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }


}
