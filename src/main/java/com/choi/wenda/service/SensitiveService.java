package com.choi.wenda.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensitiveService.class);

    /**
     * 默认敏感词替换
     * @throws Exception
     */
    private static final String DEFAULT_REPLACEMENT = "****";


    //读取文本
    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();

        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while((lineTxt = bufferedReader.readLine()) != null){
                lineTxt = lineTxt.trim();
                addWord(lineTxt);
            }
            read.close();
        }catch (Exception e){
            LOGGER.error("读取敏感词文件失败" + e.getMessage());
        }
    }
    //根
    private TrieNode rootNode = new TrieNode();

    /**
     * 判断是否是一个符号
     */
    private boolean isSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    /**
     * 过滤敏感词
     * @param text
     */
    public String filter(String text) {
        if(StringUtils.isBlank(text)){
            return text;
        }
        String replacement = DEFAULT_REPLACEMENT;
        StringBuilder result = new StringBuilder();

        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;//当前比较位置

        while(position < text.length()){
            char c = text.charAt(position);
            //如果是空格，直接跳过
            if(isSymbol(c)){
                if(tempNode == rootNode){
                    result.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            //当前位置的匹配结束
            if(tempNode == null){
                //以begin开始的字符串不存在敏感词
                result.append(text.charAt(begin));
                //跳到下一个字符开始测试
                position = begin + 1;
                begin = position;
                //回到树的初始节点
                tempNode = rootNode;
            }else if(tempNode.isKeyWordEnd()){
                //发现敏感词
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            }else{
                position++;
            }
        }
        result.append(text.substring(begin));
        return result.toString();
    }

    private void addWord(String lineTxt){
        TrieNode tempNode = rootNode;
        for(int i = 0;i < lineTxt.length();i++){
            Character c = lineTxt.charAt(i);
            // 过滤空格
            if (isSymbol(c)) {
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);

            if(node == null){
                node = new TrieNode();
                tempNode.addSubNode(c,node);
            }

            tempNode = node;

            if(i == lineTxt.length() - 1){
                tempNode.setkeywordEnd(true);
            }
        }
    }

//    public static void main(String[] argv) {
//        SensitiveService s = new SensitiveService();
//        s.addWord("色情");
//        s.addWord("好色");
//        System.out.print(s.filter("你好X色**情XX"));
//    }

    private class TrieNode{
        //是不是关键词结尾
        private boolean end = false;
        //当前节点下的子节点
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public void addSubNode(Character key, TrieNode node){
            subNodes.put(key,node);
        }

        /**
         * 获取下一个节点
         * @param key
         * @return
         */
        TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }

        boolean isKeyWordEnd(){
            return end;
        }

        void setkeywordEnd(boolean end){
            this.end = end;
        }
    }
}
