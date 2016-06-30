/*Copyright 2016 WorkStress Experiment
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package uk.ac.mdx.cs.ie.acontextlib;

import java.util.Map;

/**
 * Created by dkram on 09/06/2016.
 */
public abstract class ContextReceiver {

    private IContextManager mContextManager;
    private IOntologyManager mOntologyManager;

    public ContextReceiver() {

    }

    public ContextReceiver(IContextManager contextManager) {
        mContextManager = contextManager;
    }

    public ContextReceiver(IOntologyManager ontologyManager) {
        mOntologyManager = ontologyManager;
    }

    public ContextReceiver(IContextManager contextManager, IOntologyManager ontologyManager) {
        mContextManager = contextManager;
        mOntologyManager = ontologyManager;
    }

    public IContextManager getContextManager() {
        return mContextManager;
    }

    public IOntologyManager getOntologyManager() {
        return mOntologyManager;
    }

    public abstract void newContextValue(String name, long value);

    public abstract void newContextValue(String name, double value);

    public abstract void newContextValue(String name, boolean value);

    public abstract void newContextValue(String name, String value);

    public abstract void newContextValue(String name, Object value);

    public abstract void newContextValues(Map<String, String> values);
}
