package com.jcrawley.crosswordpuzzlesolver.trie;

public class DictionaryTrie {

    public Node rootNode;

    public DictionaryTrie(){
        this.rootNode = new Node();
    }

    public void addWord(String word){
        Node currentNode = rootNode;
        for(int i=0; i< word.length(); i++){
            String letter = String.valueOf(word.charAt(i));
            if(!currentNode.doesChildExist(letter)){
                currentNode = currentNode.addNode(letter);
            }
            else{
                currentNode = currentNode.getNode(letter);
            }
        }
    }

    public boolean doesWordExist(String word){
        Node currentNode = rootNode;
        for(int i=0; i< word.length(); i++){
            String letter = String.valueOf(word.charAt(i));
            if(!currentNode.doesChildExist(letter)){
                return false;
            }
            else{
                currentNode = currentNode.getNode(letter);
            }
        }
        return true;
    }

}
