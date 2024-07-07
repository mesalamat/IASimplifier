package de.godly.iasimplifier.util;

import java.util.Map;

public interface Serializable {


    /**
     * Each Child Class needs to return a properly serialised Map so the YAML Dumper can print it properly
     * @return a Key-Value Map that serialises the values of the Child Class
     */
    Map<String, Object> serialize();


}
