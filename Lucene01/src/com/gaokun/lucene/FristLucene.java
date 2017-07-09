package com.gaokun.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

/*
 * lucene创建索引
 */
public class FristLucene {
	/*
	 * 建索引
	 */
	@Test
	public void testLucene() throws IOException{
		//1）	创建IndexWriter对象
				Directory directory=FSDirectory.open(new File("G:\\02_北京黑马2016年7月就业班\\15、luncene、Solr使用\\Lucene&solr_day01\\index"));
				//Analyzer analyzer=new StandardAnalyzer();
				Analyzer analyzer = new IKAnalyzer();
				IndexWriterConfig indexWriterConfig=new IndexWriterConfig(Version.LATEST, analyzer);
				IndexWriter indexWriter=new IndexWriter(directory, indexWriterConfig);
		//2）	
		
		File source=new File("G:\\02_北京黑马2016年7月就业班\\15、luncene、Solr使用\\search");
		File[] fileList=source.listFiles();
		for(int i=0;i<fileList.length;i++){
			//创建Docment对象(包含域）
			Document doc=new Document();
			File f=fileList[i];
			//创建域
			String file_name=f.getName();//文件名
			System.out.println(file_name);
			Field file_nameField=new TextField("file_name",file_name , Store.YES);
			long file_size=FileUtils.sizeOf(f);//文件大小
			Field file_sizeField=new LongField("file_size",file_size,Store.YES);
			String file_content=FileUtils.readFileToString(f);//文件内容
			Field file_contentField=new TextField("file_content",file_content , Store.YES);
			String file_path=f.getPath();//文件路径
			Field file_pathField=new StringField("file_path",file_path , Store.YES);
			//添加域
			doc.add(file_nameField);
			doc.add(file_sizeField);
			doc.add(file_contentField);
			doc.add(file_pathField);
			//3将Document对象通过IndexWriter对象写入索引库中
			indexWriter.addDocument(doc);   
		}
		//4关闭IndexWriter
		indexWriter.close();
	}
	/*
	 * 查索引
	 */
	@Test
	public void testQuery() throws IOException{
		//创建directory对象，也就是索引库位置
		Directory directory=FSDirectory.open(new File("G:\\02_北京黑马2016年7月就业班\\15、luncene、Solr使用\\Lucene&solr_day01\\index"));
		//创建indexreader对象
		IndexReader indexReader =DirectoryReader.open(directory);
		//创建Indexsearcher对象
		IndexSearcher indexSearcher =new IndexSearcher(indexReader);
		//创建termquery对象，填域和关键词
		TermQuery termq=new TermQuery(new Term("file_name", "4"));
		//查询,返回结果到topdocs
		TopDocs topDocs=indexSearcher.search(termq, 4);
		//拿到评分文档数组
		ScoreDoc[] scoreDocs=topDocs.scoreDocs;
		for(int i=0;i<scoreDocs.length;i++){
			int docid=scoreDocs[i].doc;
			Document doc=indexSearcher.doc(docid);
			System.out.println(doc.get("file_name"));
		}
		indexReader.close();
	}
	// 查看标准分析器的分词效果
		@Test
		public void testTokenStream() throws Exception {
			// 创建一个标准分析器对象
//			Analyzer analyzer = new StandardAnalyzer();
//			Analyzer analyzer = new CJKAnalyzer();
//			Analyzer analyzer = new SmartChineseAnalyzer();
			Analyzer analyzer = new IKAnalyzer();
			// 获得tokenStream对象
			// 第一个参数：域名，可以随便给一个
			// 第二个参数：要分析的文本内容
//			TokenStream tokenStream = analyzer.tokenStream("test",
//					"The Spring Framework provides a comprehensive programming and configuration model.");
			TokenStream tokenStream = analyzer.tokenStream("test",
					"高富帅可以用二维表结构来逻辑表达实现的数据");
			// 添加一个引用，可以获得每个关键词
			CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
			// 添加一个偏移量的引用，记录了关键词的开始位置以及结束位置
			OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
			// 将指针调整到列表的头部
			tokenStream.reset();
			// 遍历关键词列表，通过incrementToken方法判断列表是否结束
			while (tokenStream.incrementToken()) {
				// 关键词的起始位置
				System.out.println("start->" + offsetAttribute.startOffset());
				// 取关键词
				System.out.println(charTermAttribute);
				// 结束位置
				System.out.println("end->" + offsetAttribute.endOffset());
			}
			tokenStream.close();
		}
}
