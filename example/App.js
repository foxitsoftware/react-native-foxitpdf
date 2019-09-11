/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  NativeModules,
  TouchableOpacity,
} from 'react-native';
import FoxitPDF from '@foxitsoftware/react-native-foxitpdf';
import uiextensions_config from './uiextensions_config.json';

type Props = {};
export default class App extends Component<Props> {

  constructor(props) {
     super(props);

     FoxitPDF.initialize("nt4MfRHH+05GEO/9DEJZFuh1shwOhyHfo89Vki8tNbGJSk5cUHnHnQ==","ezJvjl3GtG539PsXZqXcImmEXhuiodACWf3giDcZ4Qnhp7nnD+Ya2PnVL+yd393PG9ZR8mfhQ3Ux2tdnplM1VSpe2Kms0pk4/0WEGhV9RtjvWd4XO1hUaeeY4DrFXN7uNRyNCl2G6kLGvlv59kMq56qflOI/2688le9VBLwtaQk05dkdrQ65e6yjDN2PQmV5QZcVLfi7vVlRvQnUIB6vtFrY4fCrycXaBG2/3Gw1v7CzvboDTiiheUDT7GqSfAY1Y3ITf2HTw7OC5KU1m7qVWia2V4VQkT/l1JaEAk5Fe6ZZBd3jPGpj7GXcBIXrUdEfJQj1qbEVB5OFO8peNA/s851LhBkcRN25wydMJG4Bsbdzm0Kp8gIL3DKf6W0aI6nsFyUc8EeUpkYY6HN2MoUg/OJsbqotaBI0cx6NZu038s3gEBBhqUGSHO5x7dzDMojU6jRADpfs6fqOFn2kQIk9JCq7fPyBzJBHFsRmh3eJ6JnEzMfjrZQLQ1Hbj+/74EzDqydz1AoPdSZYfXQ4HlGClYCKEpaVgpJhemYQxS43kWECQ4Kwa/99U6IxYWKoQ+Jm/DmABbcc8V086c9rHd5ClDFWk2PQ1BsGWSHFeuwW/sauDu+IPsAEukusIiP7DcN6J9rkmnPW2exgZKVpQlF3sqwIkm9XKxiFwuEdq+9eRI1e8d7C+91cFr9sc0idkkJCloLjUqfNunjeKVKU49Kyhp8kc1fdI06iMrg0kMzorewqnkjwqLEigFAxEOO1LyDXDTTuy4DFPX+ggVaqnmAicA8bqMlndMqWARpED8VOnjTNlNsZXf5xgJiYz1WfcVbIvTqKEITo1zxivuTCpfM+h3Sra0kH0LJmKiTsvnHsPKHDbgswwBZaUByO2cmRxw5bj12ncfLBV41NCalG84ocgck1LhIshHde0n9zp6ufuctCtkfnpM5iP5qTsTZpiyyKQ5YbVNN15DEN50JKh9KZXGXSJoLTdLfRIgalXttN0g+w21m8JegLZl483vl3hm2L7q9HUnJ/gP9MilIbK2JnV49rMte5MH7qBxFmqwOH3RfgARsjsfju8MLDJg3NYAzfUjPyLyJpVX7VvqLe5RO99jVPXnKCRPXzCq4wDwVYEmRSHjc7sgUyS2UOUf/aFNZKG6284yrNCPqaPmUjvtbmEXJyPW5QMUN+mJv2/uBm3FUUqiJcbUeM+fVqM8c7aazA2zw0f43JNBMSgn8RbEbph4L86polwCK8YVamUuWUsw==");
  }

  onPress() {
    FoxitPDF.openDocument('/sample.pdf','',uiextensions_config);
  }

  render() {
    return (
      <View style={styles.container}>
        <TouchableOpacity onPress={this.onPress}>
          <Text>Open PDF</Text>
        </TouchableOpacity>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
});
