# Introduction:

This project is related to the extraction of product features from free text containing customer reviews. For example, if a customer writes a review about a mobile on Amazon, the mobile is a product and its features can be screen size, battery life, etc. if he writes about them in his review.

# Algorithm:

After the results are obtained by taking the most frequent x words which are not words like stopwords, prepositions, pronouns etc. (which can be done by getting their POS tags), I take them in a list and calculate its synonyms using WordNet-3.1.If the synonyms found are among the product features, then they are removed from the original list. The actual algorithm is slightly more complex than what is mentioned above , however, the gist is the same. 
