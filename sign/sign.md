http://androidxref.com/4.4_r1/xref/build/tools/signapk/SignApk.java
addDigestsToManifest() 生成MANIFEST.MF文件
writeSignatureFile() 生成CERT.SF文件

###MANIFEST.MF


Manifest-Version: 1.0
Created-By: 1.0 (Android)

Name: res/layout/main.xml
SHA1-Digest: TKJzyMwELyakLZYM83o10LERyPQ=

sha1加密后再进行base64编码

```
	MessageDigest digest = MessageDigest.getInstance("SHA1");
	
	byte[] buffer = new byte[4096];
	BufferedInputStream bis = new BufferedInputStream(new FileInputStream("jyn.xml"));
	int n = 0;
	while((n = bis.read(buffer)) > 0) {
		digest.update(buffer, 0, n);
	}
	bis.close();
	String abc = new String(Base64.getEncoder().encode(digest.digest()));
	System.out.println(abc);
```


###CERT.SF

Signature-Version: 1.0
Created-By: 1.0 (Android)
SHA1-Digest-Manifest: Uin+pH/oQLOt1Esnw9TTJpf8URc=

Name: res/layout/main.xml
SHA1-Digest: +zm+W/d5nXnQRHhQq1BeXsj4sWU=


多了一项SHA1-Digest-Manifest的值，这个值就是MANIFEST.MF文件的SHA-1并base64编码后的值。

````
	StringBuffer sb = new StringBuffer();
	sb.append("Name: res/layout/main.xml");
	sb.append("\r\n");
	sb.append("SHA1-Digest: TKJzyMwELyakLZYM83o10LERyPQ=");
	sb.append("\r\n");
	sb.append("\r\n");
	
	try {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		byte[] buffer = sb.toString().getBytes();
		md.update(buffer);
		
		String abc = new String(Base64.getEncoder().encode(md.digest()));
		System.out.println(abc);
	} catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	}
	
````

###CERT.RSA


```
	FileInputStream fis;
	try {
		fis = new FileInputStream("META-INF/ANDROIDR.RSA");
		PKCS7 pkcs7 = new PKCS7(fis);
		X509Certificate publicKey = pkcs7.getCertificates()[0];
		System.out.println("issuer1:" + publicKey.getIssuerDN());
		System.out.println("subject2:" + publicKey.getSubjectDN());
		System.out.println(publicKey.getPublicKey());
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ParsingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
```





###X.509格式
![关联图片](http://img.blog.csdn.net/20140603223608734?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvWERfbGl4aW4=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast "这是关联图片")


