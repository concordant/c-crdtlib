The purpose of this document is to summarize competitors interfaces, in order to derive our
interface for the CRDTValueMap.


# Parse

Website: <https://parseplatform.org/>

Github: <https://github.com/parse-community/>

Parse is an objects and files storage.

Parse supports different platforms:
* GraphQL
* iOS
* Android
* JavaScript
* .NET + Xamarin
* macOS
* Unity
* PHP
* Arduino
* Embedded C
* Cloud Code
* REST

Object in sense can be seen has a map.

For each platform methods' names are selected to fit to the standard map names. For example
_map.set(key, value)_ will be used to create/update values in JavaScript, where _map.put()_ is
preferred for Android, and _map[key] = value_ for iOS.


## JavaScript API

Link to documentation: <https://docs.parseplatform.org/js/guide/>.

The object provides the following methods:
* _set(key, value)_: creates or updates the value associated to the key.
* _get(key)_: gets the value associated to the key.

There is no way to remove keys, but it is possible to _set(key, null)_.
A key is associated to type it has set forever.

For number values:
* _increment(key, [inc])_: increment number by _inc_. Default value of _inc_ is 1. It is possible to
decrement by passing a negative value.

For array values:
* _add(key, elem)_: adds _elem_ to the end of the array.
* _addUnique(key, elem)_: adds _elem_ to the array iff it is not already present.
* _remove(key, elem)_: removes _elem_ from the array.

It is possible to query object from the cloud by using _query.get(objectId)_.

Objects provide _save()_ and _fetch()_ methods to save/update them from/to the cloud.

## Android API

Link to documentation: <https://docs.parseplatform.org/android/guide/>.

The object provides the following methods:
* _put(key, value)_: creates or updates the value associated to the key.
* _getXXXX(key)_: gets the value (casted as type XXXX) associated to the key.
* _remove(key)_: removes the value associated to the key.
* _delete()_: deletes the object.

A type associated to a key is fix in time except maybe if the key is removed (but it is not clear in
the documentation). It is still possible to _put(key, null)_.

For numbers values:
* _increment(key, [inc])_: increment number by _inc_. Default value of _inc_ is 1. It is possible to
decrement by passing a negative value.

For array values:
* _add(key, elem)_: adds _elem_ to the end of the array.
* _addAll(key, elems)_: adds all _elems_ to the end of the array.
* _addUnique(key, elem)_: adds _elem_ to the array iff it is not already present.
* _addAllUnique(key, elems)_: adds all _elems_ to the array iff they are not already present.
* _removeAll(key, elems)_: removes _elem_ from the array.

It is possible to query object from the cloud by using _query.get(objectId)_.

Objects provide _save()_ and _fetch()_ methods to save/update them from/to the cloud.

There exist _pin()_ and _unpin()_ methods to store objects locally, and the corresponding
_get(objectId)_, _save()_, _fetch()_ methods. The _saveEventually()_ method works locally until the
connection with the cloud is possible.


## iOS API

Link to documentation <https://docs.parseplatform.org/ios/guide/>.

This API exists for Objective-C and Swift.

The object provides the following possibilities:
* _map[key] = value_: creates or updates the value associated to the key.
* _value <- map[key]_: gets the value associated to the key, this value should be cast.
* _delete()_: deletes the object.

A type associated to a key is fix in time.

For number values:
* _increment(key, [inc])_: increment number by _inc_. Default value of _inc_ is 1. It is possible to
decrement by passing a negative value.

For array values:
* _addObject_: adds one value to the array.
* _addObjects_: adds multiple values to the array.
* _addObjectUnique_: adds one value to the array iff it is not already in.
* _addObjectsUnique_: adds multiple values to the array iff it is not already in.
* _removeObject_: removes one value from the array.
* _removeObjects_: removes multiple values from the array.

There are some _query.get()_, _save()_, and _fetch()_ functions to communicate with the cloud.

There are also _pin()_, _unpin()_ to store objects locally, and the corresponding _query.get()_,
_save()_, _fetch()_. The _saveEventually()_ method works locally until the connection with the cloud
is possible.


# PouchDB

Website: <https://pouchdb.com/>

Github: <https://github.com/pouchdb/pouchdb>

PouchDB is a NoSQL key-value store library in JavaScript. The data model is JSON document.

It provides a smooth synchronization between the local browser in-memory database and CouchDB remote
cloud storage.

Link to documentation: <https://puchdb.com/api.html>

API description:
* _get(id)_: returns the full state of the query document.
* _put(id, doc)_: creates or updates the corresponding document.
* _remove(doc)_: removes a given document.

Document can be easily edited since JSON document are easy to manipulate in JavaScript.


# YJS

Website: <http://y-js.org/>

Github: <https://github.com/yjs/yjs>

It is state-based CRDT library in JavaScript providing propagation.

The library provides a map implementation.

Types accepted as value are:
* object
* boolean
* string
* number
* Uint8Array
* Y.Type (other CRDTs)

To manipulate key-value use the following methods:
* _get(key)_: gets the value associated with the key.
* _set(key, value)_: put or updates the key with the given value.
* _delete(key)_: deletes a given key.
* _has(key)_: returns true if the map contains the key false otherwise.

Map provides iterator methods: _entries()_, _keys()_, and _values()_.

Map provides an observer mechanism for the different keys.


# IPFS Delta-CRDT

Github: <https://github.com/peer-base/js-delta-crdts/>

IPFS delta-crdt is a library of delta-crdts in JavaScript.

IPFS provides an Observed-Remove-map (ORmap) supporting the embedding of the following CRDT types:
* AWORSet
* CCounter
* DWFlag
* EWFlag
* MVReg
* ORMap
* RWORSet

This map provides the following methods:
* _applySub(key, typeName, mutatorName, ...args)_: applies the _mutatorName(...args)_
function on the value associated to the key. This method return a delta.
* _apply(delta)_: applies a given delta to the replica.
* _remove(key)_: removes the value associated to the key. This method returns a delta.
* _value()_: returns the full state of the map, as JSON document.
* _join(s1, s2)_: static method to merge two replicas.
